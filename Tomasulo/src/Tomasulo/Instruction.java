package Tomasulo;

public class Instruction {
	String type;
	String dest;
	String src1;
	String src2;
	int latency;
	int line;
	public void copy(Instruction it)
	{
		this.type=it.type;
		this.dest=it.dest;
		this.src1=it.src1;
		this.src2=it.src2;
		this.latency=it.latency;
	}
	}
