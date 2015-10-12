package org.gcn.plinguacore.util;

import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteOrderDataOutputStream extends FilterOutputStream implements
		DataOutput {
	private ByteBuffer buf;
	private DataOutputStream out;
	public ByteOrderDataOutputStream(OutputStream out,ByteOrder order) {
		super(null);
		this.out=new DataOutputStream(out);
		buf = ByteBuffer.allocate(8);
		buf.order(order);
		// TODO Auto-generated constructor stub
	}
	private void writeBuffer() throws IOException
	{
		int size=buf.position();
		buf.rewind();
		for (int i=0;i<size;i++)
			out.writeByte(buf.get());
	}
	
	public static int getAccuracy(long n)
	{
		if (n<0L)
			throw new IllegalArgumentException();
		if (n<=0xFFL)
			return 0;
		else
		if (n<=0xFFFFL)
			return 1;
		else
		if (n<=0xFFFFFFFFL)
			return 2;
		else
			return 3;
	}
	
	public void writeNumber(long n,int accuracy) throws IOException
	{
		switch(accuracy)
		{
		case 0:
			writeByte((byte)n);
			break;
		case 1:
			writeChar((char)n);
			break;
		case 2:
			writeInt((int)n);
			break;
		default:
			writeLong(n);
				
		}
		
		
			
	}
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		out.close();
	}



	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub
		out.flush();
	}



	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		// TODO Auto-generated method stub
		out.write(b, off, len);
	}



	@Override
	public void write(byte[] b) throws IOException {
		// TODO Auto-generated method stub
		out.write(b);
	}



	@Override
	public void write(int b) throws IOException {
		// TODO Auto-generated method stub
		out.write(b);
	}



	@Override
	public void writeBoolean(boolean v) throws IOException {
		// TODO Auto-generated method stub
		out.writeBoolean(v);
	}

	@Override
	public void writeByte(int v) throws IOException {
		// TODO Auto-generated method stub
		out.writeByte(v);
	}

	@Override
	public void writeBytes(String s) throws IOException {
		// TODO Auto-generated method stub
		out.writeBytes(s);
	}

	@Override
	public void writeChar(int v) throws IOException {
		// TODO Auto-generated method stub
		buf.clear();
		buf.putChar((char)v);
		writeBuffer();
		
	}

	@Override
	public void writeChars(String s) throws IOException {
		// TODO Auto-generated method stub
		byte []bytes=s.getBytes();
		for (int i=0;i<bytes.length;i++)
			writeChar(bytes[i]);
	}

	@Override
	public void writeDouble(double v) throws IOException {
		// TODO Auto-generated method stub
		buf.clear();
		buf.putDouble(v);
		writeBuffer();
	}

	@Override
	public void writeFloat(float v) throws IOException {
		// TODO Auto-generated method stub
		buf.clear();
		buf.putFloat(v);
		writeBuffer();
	}

	@Override
	public void writeInt(int v) throws IOException {
		// TODO Auto-generated method stub
		buf.clear();
		buf.putInt(v);
		writeBuffer();
	}

	@Override
	public void writeLong(long v) throws IOException {
		// TODO Auto-generated method stub
		buf.clear();
		buf.putLong(v);
		writeBuffer();
	}

	@Override
	public void writeShort(int v) throws IOException {
		// TODO Auto-generated method stub
		buf.clear();
		buf.putShort((short)v);
		writeBuffer();
	}

	@Override
	public void writeUTF(String s) throws IOException {
		// TODO Auto-generated method stub
		out.writeUTF(s);
	}

}
