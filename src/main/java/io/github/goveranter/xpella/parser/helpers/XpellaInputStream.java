package io.github.goveranter.xpella.parser.helpers;

import java.util.HashMap;
import java.util.Map;

public class XpellaInputStream
{
	private String code;
	private int currentPosition;
	private int currentLine;
	private int currentColumn;
	private Map<Integer, Integer> lineColumns;
	
	public XpellaInputStream(String code)
	{
		this.code = code;
		this.currentPosition = 0;
		this.currentLine = 1;
		this.currentColumn = 0;
		this.lineColumns = new HashMap<>();
	}
	
	public boolean isEof()
	{
		return this.code.length() == this.currentPosition;
	}
	
	public String next()
	{
		if(this.isEof())
			return null;
		else
		{
			String curChar = this.code.substring(this.currentPosition, this.currentPosition + 1);
			this.currentPosition++;
			if(curChar.equals("\n"))
			{
				this.lineColumns.put(this.currentLine, this.currentColumn);
				this.currentLine++;
				this.currentColumn = 0;
			}
			else
				this.currentColumn++;
				
			return curChar;
		}
	}
	
	public String peek()
	{
		if(this.isEof())
			return null;
		else
			return this.code.substring(this.currentPosition, this.currentPosition + 1);
	}
	
	public String peekTo(int toAdd)
	{
		if (this.currentPosition + toAdd > this.code.length())
			return null;
		else
			return this.code.substring(this.currentPosition + toAdd, this.currentPosition + toAdd + 1);
	}
	
	public String peekBack()
	{
		if (this.currentPosition == 0)
			return null;
		else
			return this.code.substring(this.currentPosition - 1, this.currentPosition);
	}
	
	public int getCurrentPosition() { return this.currentPosition; }
	public int getCurrentLine() { return this.currentLine; }
	public int getCurrentColumn() { return this.currentColumn; }
	
	public void rewind(int toPosition)
	{
		toPosition = toPosition < 0 ? 0 : toPosition;
		while (this.currentPosition != toPosition)
		{
			this.currentPosition--;
			if (this.peek().equals("\n"))
			{
				this.currentLine--;
				this.currentColumn = this.lineColumns.get(this.currentLine);
			}
			else
				this.currentColumn--;
		}
	}
}
