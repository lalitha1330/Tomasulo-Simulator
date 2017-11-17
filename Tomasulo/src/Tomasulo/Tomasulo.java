package Tomasulo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Tomasulo {


	static int num_add_rs;
	static int num_mult_rs;
	static int num_ld_rs;
	static int br_num;
	static int num_add_unit;
	static int num_mult_unit;
	static int num_ld_unit;
	static int num_br_unit;
	static int num_mem_unit;
	static int add_latency;
	static int sub_latency;
	static int mult_latency;
	static int div_latency;
	static int ld_latency;
	static int st_latency;
	static int br_latency;
	static ArrayList<String> inst=new ArrayList<String>();
	static Instruction iq[];
	static int inst_num;	
	static int cycles;
	static int inst_count;
	static RS[] add_rs;
	static RS[] mult_rs;
	static RS[] ld_rs;
	static RS[] br_rs;
	static int k;
	static FU[] fu_add;
	static FU[] fu_mult;
	static FU[] fu_ld;
	static FU[] fu_br;
	static CDB cdb;
	static MEM[] mem;
	static int num_inst;
	static String branch_result;
	static int number;
	static int z;

	//Creating the architecture
	public static void arch()
	{
		int i;
		add_rs=new RS[num_add_rs];
		mult_rs=new RS[num_mult_rs];
		ld_rs=new RS[num_ld_rs];
		br_rs=new RS[br_num];
		fu_add=new FU[num_add_unit];
		fu_mult=new FU[num_mult_unit];
		fu_ld=new FU[num_ld_unit];
		fu_br=new FU[num_br_unit];
		cdb=new CDB();
		mem=new MEM[num_mem_unit];
		
		for (i = 0; i < num_add_rs; i++) {
				add_rs[i]=new RS();
				add_rs[i].status="AVAILABLE";
				add_rs[i].timer = 0;
				add_rs[i].Qj = add_rs[i].Qk = null;
			}
		for (i = 0; i < num_mult_rs; i++) {
			    mult_rs[i]=new RS();
				mult_rs[i].status = "AVAILABLE";
				mult_rs[i].timer = 0;
				mult_rs[i].Qj = mult_rs[i].Qk = null;
			}
		
		for (i = 0; i < num_ld_rs; i++) {
			    ld_rs[i]=new RS();
				ld_rs[i].status = "AVAILABLE";
				ld_rs[i].timer = 0;
				ld_rs[i].Qj = ld_rs[i].Qk = null;
			}
		for (i = 0; i < br_num; i++) {
		br_rs[i]=new RS();
		br_rs[i].status= "AVAILABLE";
		br_rs[i].timer = 0;
		br_rs[i].Qj = br_rs[i].Qk = null;
		}
		for(i=0;i<num_add_unit;i++)
		{
			fu_add[i]=new FU();
			fu_add[i].status="EMPTY";
		}
		for(i=0;i<num_mult_unit;i++)
		{
			fu_mult[i]=new FU();
			fu_mult[i].status="EMPTY";
		}
		for(i=0;i<num_ld_unit;i++)
		{
			fu_ld[i]=new FU();
			fu_ld[i].status="EMPTY";
		}
		for(i=0;i<num_br_unit;i++)
		{
			fu_br[i]=new FU();
			fu_br[i].status="EMPTY";
		}
		for(i=0;i<num_mem_unit;i++)
		{
			mem[i]=new MEM();
			mem[i].status="EMPTY";
		}
}
@SuppressWarnings("null")

//ISSUE STAGE

public static void issue () {
	int i;
		RS[] rs_type=null;
		int rs_count=0;
		Instruction curr= new Instruction();
				curr=iq[inst_num];	
		if (curr.type.equals("ADD") || curr.type.equals("SUB"))
				{
			rs_count = num_add_rs; rs_type = add_rs;
			for (i = 0; i < rs_count; i++)
				if (rs_type[i].status == "AVAILABLE")
					break;
			if (i >= rs_count) return;
			add_rs[i].status = "BUSY";
			add_rs[i].type=curr.type;
			add_rs[i].line=curr.line;
			add_rs[i].t=curr;
			inst_num++;
			if (curr.src1!=null)
				add_rs[i].Qj = dest_check(curr.src1);

			if (curr.src2!=null)
				add_rs[i].Qk = dest_check(curr.src2);
			if(inst.contains(curr.dest))
					inst.removeAll(Collections.singleton(curr.dest));
		}
		else if (curr.type.equals("MULT") || curr.type.equals("DIV")) {
			rs_count = num_mult_rs; rs_type = mult_rs;
			for (i = 0; i < rs_count; i++)
				if (rs_type[i].status == "AVAILABLE")
					break;
			if (i >= rs_count) return;	
			mult_rs[i].status = "BUSY";
			mult_rs[i].type=curr.type;
			mult_rs[i].line=curr.line;
			mult_rs[i].t=curr;
			inst_num++;
			if (curr.src1!=null)
				mult_rs[i].Qj = dest_check(curr.src1);
			if (curr.src2!=null)
				mult_rs[i].Qk = dest_check(curr.src2);
			if(inst.contains(curr.dest))
				inst.removeAll(Collections.singleton(curr.dest));
		}
		else if (curr.type.equals("LW") || curr.type.equals("SW")) {
			rs_count = num_ld_rs; rs_type = ld_rs;
			for (i = 0; i < rs_count; i++)
				if (rs_type[i].status == "AVAILABLE")
					break;
			if (i >= rs_count) return;
			ld_rs[i].status = "BUSY";
			ld_rs[i].type=curr.type;
			ld_rs[i].line=curr.line;
			ld_rs[i].t=curr;
			inst_num++;
			if (curr.src1!=null)
			ld_rs[i].Qj = dest_check(curr.src1);

			if (curr.src2!=null)
				ld_rs[i].Qk = dest_check(curr.src2);
			if(curr.type.equals("LW"))
			{if(inst.contains(curr.dest))
				inst.removeAll(Collections.singleton(curr.dest));}
		}
		else 
		{
			for (i = 0; i < br_num; i++)
				if (br_rs[i].status == "AVAILABLE")
					break;
			if (i >= br_num) return;
			br_rs[i].status = "BUSY";
			br_rs[i].type=curr.type;
			br_rs[i].line=curr.line;
			br_rs[i].t=curr;
			inst_num++;
			if (curr.src1!=null)
			br_rs[i].Qj = dest_check(curr.src1);

			if (curr.src2!=null)
				br_rs[i].Qk = dest_check(curr.src2);
	}
return;
}

//Dependency Check
public static String dest_check(String s)
{
	int instr = inst_num-1;

	while(instr>0){
		if(s.equals(iq[instr-1].dest)&&(!inst.contains(s))) {
			return iq[instr-1].dest;
		}
		instr--;
	}

	return null;
}

//Execute Stage
public static void execute()
{
	rs_exec(add_rs,num_add_rs,0);
 	rs_exec(mult_rs,num_mult_rs,1);
	rs_exec(ld_rs,num_ld_rs,2);
	rs_exec(br_rs,br_num,3);
}

public static void rs_exec(RS[] stn, int n, int m)
{   
	switch(m){
		case 0 : 
		{for(int i=0;i<n;i++)
		{
			if("ADD".equals(add_rs[i].type))
			{
				add_rs[i].latency=add_latency;
				if(add_rs[i].status=="BUSY"){
					if((add_rs[i].Qj==null||(inst.contains(add_rs[i].Qj)))&& (add_rs[i].Qk==null||(inst.contains(add_rs[i].Qk)))){
						if(add_rs[i].line<number*z){
						if(add_rs[i].timer==0){
							add_rs[i].timer=add_rs[i].latency;
							int j;
							for(j=0;j<num_add_unit;j++)
							{
				            if(fu_add[j].status=="EMPTY")
				            break;
				            else continue;
							}
							if(j!=num_add_unit)
							{fu_add[j].status="FULL";
							fu_add[j].f=add_rs[i].t;
							add_rs[i].timer--;
							if(add_rs[i].timer==0)
						    {add_rs[i].status="RESULT_READY";
						    fu_add[j].num=1;}
						    }
						    }
						else
							{add_rs[i].timer--;
							if(add_rs[i].timer==0)
							{add_rs[i].status="RESULT_READY";
							int j;
							for(j=0;j<num_add_unit;j++)
							{
				            if(fu_add[j].f==add_rs[i].t)
				            break;
				            else continue;
							}
							if(j!=num_add_unit)
							{
							fu_add[j].num=1;}
							}
							}
					}
					}
				}
			}
			else if("SUB".equals(add_rs[i].type))
			{
				add_rs[i].latency=sub_latency;
				if(add_rs[i].status=="BUSY"){
					if((add_rs[i].Qj==null||(inst.contains(add_rs[i].Qj)))&& (add_rs[i].Qk==null||(inst.contains(add_rs[i].Qk)))){
						if(add_rs[i].line<number*z){
						if(add_rs[i].timer==0){
							add_rs[i].timer=add_rs[i].latency;
							int j;
							for(j=0;j<num_add_unit;j++)
							{
				            if(fu_add[j].status=="EMPTY")
				            break;
				            else continue;
							}
							if(j!=num_add_unit)
							{fu_add[j].status="FULL";
							fu_add[j].f=add_rs[i].t;
							add_rs[i].timer--;
							if(add_rs[i].timer==0)
						    {add_rs[i].status="RESULT_READY";
						    fu_add[j].num=1;}
						    }
						    }
						else
							{add_rs[i].timer--;
							if(add_rs[i].timer==0)
							{add_rs[i].status="RESULT_READY";
							int j;
							for(j=0;j<num_add_unit;j++)
							{
				            if(fu_add[j].f==add_rs[i].t)
				            break;
				            else continue;
							}
							if(j!=num_add_unit)
							{
							fu_add[j].num=1;}
							}
							}
					}
					}
				}
			}
		}
		for(int i=0;i<num_add_unit;i++)
		{
			if(fu_add[i].num==1){
					fu_add[i].status="EMPTY";
			fu_add[i].num=0;}
		}
}						
break;
	case 1 : {for(int i=0;i<n;i++)
	{
		if("MULT".equals(mult_rs[i].type))
		{
			mult_rs[i].latency=mult_latency;
			if(mult_rs[i].status=="BUSY"){
				if((mult_rs[i].Qj==null||(inst.contains(mult_rs[i].Qj)))&& (mult_rs[i].Qk==null||(inst.contains(mult_rs[i].Qk)))){
					if(mult_rs[i].line<number*z){
					if(mult_rs[i].timer==0){
						mult_rs[i].timer=mult_rs[i].latency;
						int j;
						for(j=0;j<num_mult_unit;j++)
						{
			            if(fu_mult[j].status=="EMPTY")
			            break;
			            else continue;
						}
						if(j!=num_mult_unit)
						{fu_mult[j].status="FULL";
						fu_mult[j].f=mult_rs[i].t;
						mult_rs[i].timer--;
						if(mult_rs[i].timer==0)
					    {mult_rs[i].status="RESULT_READY";
					    fu_mult[j].num=1;}
					    }
					    }
					else
						{mult_rs[i].timer--;
						if(mult_rs[i].timer==0)
						{mult_rs[i].status="RESULT_READY";
						int j;
						for(j=0;j<num_mult_unit;j++)
						{
			            if(fu_mult[j].f==mult_rs[i].t)
			            break;
			            else continue;
						}
						if(j!=num_mult_unit)
						{
						fu_mult[j].num=1;}
						}
						}
				}
				}
			}
		}
		else if("DIV".equals(mult_rs[i].type))
		{
			mult_rs[i].latency=div_latency;
			if(mult_rs[i].status=="BUSY"){
				if((mult_rs[i].Qj==null||(inst.contains(mult_rs[i].Qj)))&& (mult_rs[i].Qk==null||(inst.contains(mult_rs[i].Qk)))){
					if(mult_rs[i].line<number*z){
					if(mult_rs[i].timer==0){
						mult_rs[i].timer=mult_rs[i].latency;
						int j;
						for(j=0;j<num_mult_unit;j++)
						{
			            if(fu_mult[j].status=="EMPTY")
			            break;
			            else continue;
						}
						if(j!=num_mult_unit)
						{fu_mult[j].status="FULL";
						fu_mult[j].f=mult_rs[i].t;
						mult_rs[i].timer--;
						if(mult_rs[i].timer==0)
					    {mult_rs[i].status="RESULT_READY";
					    fu_mult[j].num=1;}
					    }
					    }
					else
						{mult_rs[i].timer--;
						if(mult_rs[i].timer==0)
						{mult_rs[i].status="RESULT_READY";
						int j;
						for(j=0;j<num_mult_unit;j++)
						{
			            if(fu_mult[j].f==mult_rs[i].t)
			            break;
			            else continue;
						}
						if(j!=num_mult_unit)
						{
						fu_mult[j].num=1;}
						}
						}
				}
				}
			}
			}	
	}
	for(int i=0;i<num_mult_unit;i++)
	{
		if(fu_mult[i].num==1)
				{fu_mult[i].status="EMPTY";
		fu_mult[i].num=0;}
	}
}	
	break;
	case 2 : {for(int i=0;i<n;i++)
	{
		if("LW".equals(ld_rs[i].type))
		{
			ld_rs[i].latency=ld_latency;
			if(ld_rs[i].status=="BUSY"){
				if((ld_rs[i].Qj==null||(inst.contains(ld_rs[i].Qj)))&& (ld_rs[i].Qk==null||(inst.contains(ld_rs[i].Qk)))){
					if(ld_rs[i].line<number*z){
						if(ld_rs[i].timer==0){
						ld_rs[i].timer=ld_rs[i].latency;
						int j;
						for(j=0;j<num_ld_unit;j++)
						{
			            if(fu_ld[j].status=="EMPTY")
			            break;
			            else continue;
						}
						if(j!=num_ld_unit)
						{fu_ld[j].status="FULL";
						fu_ld[j].f=ld_rs[i].t;
						ld_rs[i].timer--;
						if(ld_rs[i].timer==0)
					    {ld_rs[i].status="MEM";
					    fu_ld[j].num=1;}
					    }
					    }
					else
						{ld_rs[i].timer--;
						if(ld_rs[i].timer==0)
						{ld_rs[i].status="MEM";
						int j;
						for(j=0;j<num_ld_unit;j++)
						{
			            if(fu_ld[j].f==ld_rs[i].t)
			            break;
			            else continue;
						}
						if(j!=num_ld_unit)
						{
						fu_ld[j].num=1;}
						}
						}
				}
				}
			}
		}
		else if("SW".equals(ld_rs[i].type))
		{
			ld_rs[i].latency=st_latency;
			if(ld_rs[i].status=="BUSY"){
				if((ld_rs[i].Qj==null||(inst.contains(ld_rs[i].Qj)))&& (ld_rs[i].Qk==null||(inst.contains(ld_rs[i].Qk)))){
					if(ld_rs[i].line<number*z){
						if(ld_rs[i].timer==0){
						ld_rs[i].timer=ld_rs[i].latency;
						int j;
						for(j=0;j<num_ld_unit;j++)
						{
			            if(fu_ld[j].status=="EMPTY")
			            break;
			            else continue;
						}
						if(j!=num_ld_unit)
						{fu_ld[j].status="FULL";
						fu_ld[j].f=ld_rs[i].t;
						ld_rs[i].timer--;
						if(ld_rs[i].timer==0)
					    {ld_rs[i].status="MEM";
					    fu_ld[j].num=1;}
					    }
					    }
					else
						{ld_rs[i].timer--;
						if(ld_rs[i].timer==0)
						{ld_rs[i].status="MEM";
						int j;
						for(j=0;j<num_ld_unit;j++)
						{
			            if(fu_ld[j].f==ld_rs[i].t)
			            break;
			            else continue;
						}
						if(j!=num_ld_unit)
						{
						fu_ld[j].num=1;}
						}
						}
				}
				}
			}
	}
	}
	for(int i=0;i<num_ld_unit;i++)
	{
		if(fu_ld[i].num==1){
				fu_ld[i].status="EMPTY";
fu_ld[i].num=0;}
	}
}	
	break;
	case 3 : {for(int i=0;i<n;i++)
	{
		br_rs[i].latency=br_latency;
		if(br_rs[i].status=="BUSY"){
			if((br_rs[i].Qj==null||(inst.contains(br_rs[i].Qj)))&& (br_rs[i].Qk==null||(inst.contains(br_rs[i].Qk)))){
				if(br_rs[i].timer==0){
					br_rs[i].timer=br_rs[i].latency;
					int j;
					for(j=0;j<num_br_unit;j++)
					{
		            if(fu_br[j].status=="EMPTY")
		            break;
		            else continue;
					}
					if(j!=num_br_unit)
					{fu_br[j].status="FULL";
					fu_br[j].f=ld_rs[i].t;
					br_rs[i].timer--;
					if(br_rs[i].timer==0)
				    {br_rs[i].status="AVAILABLE";z++;
				    fu_br[j].num=1;}
				    }
				    }
				else
					{br_rs[i].timer--;
					if(br_rs[i].timer==0)
					{br_rs[i].status="AVAILABLE";z++;
					int j;
					for(j=0;j<num_br_unit;j++)
					{
		            if(fu_br[j].f==br_rs[i].t)
		            break;
		            else continue;
					}
					if(j!=num_br_unit)
					{
					fu_br[j].num=1;}
					}
					}
			}
		}
	}
for(int i=0;i<num_br_unit;i++)
{
	if(fu_br[i].num==1){
			fu_br[i].status="EMPTY";
fu_br[i].num=0;}
}	
}
break;
}
}

//Memory stage
 public static void memory()
 {
	 {for(int i=0;i<num_ld_rs;i++)
		{ 
		 if(ld_rs[i].status=="MEM")
		 {
		   int j=0;
		   for(j=0;j<num_mem_unit;j++)
		   {
			   if(mem[j].status=="EMPTY")
			   break;
			   else continue;
		   }
		   if(j!=num_mem_unit)
			{mem[j].status="FULL";
			mem[j].v=ld_rs[i].t;
			if("LW".equals(ld_rs[i].type))
			ld_rs[i].status="RESULT_READY";
			else ld_rs[i].status="AVAILABLE";
		    mem[j].num=1;}
		    } 
		 }
		}
 for(int i=0;i<num_mem_unit;i++)
	{
		if(mem[i].num==1){
				mem[i].status="EMPTY";
           mem[i].num=0;}
	}
}
 
//Writeback stage

public static void writeback()
{
	PriorityQueue<RS> pq = new PriorityQueue<RS>(1000, new Comparator<RS>() {  
	    
        public int compare(RS w1, RS w2) {                         
            if(w1.line==w2.line)
            	return 0;
            else if (w1.line>w2.line)
            	return 1;
            else return -1;
        }      
    });
	for(int i=0;i<num_add_rs;i++)
	{
	if(add_rs[i].status=="RESULT_READY")
	{
		add_rs[i].i=i;
		pq.add(add_rs[i]);
	}   
    }
	for(int i=0;i<num_mult_rs;i++)
	{
	if(mult_rs[i].status=="RESULT_READY")
	{
		mult_rs[i].i=i;
    pq.add(mult_rs[i]);
    }
	}
	for(int i=0;i<num_ld_rs;i++)
	{	
	if(ld_rs[i].status=="RESULT_READY")
	{
		ld_rs[i].i=i;
    pq.add(ld_rs[i]);
    }
	}
	RS rs=new RS();
	if(pq.size()==0)
	return;
	rs=pq.poll();
	int n=0;
	if(rs.type.equals("ADD")||rs.type.equals("SUB"))
		n=0;
	if(rs.type.equals("MULT")||rs.type.equals("DIV"))
		n=1;
	if(rs.type.equals("LW")||rs.type.equals("SW"))
		n=2;
	rs_write(rs,n);
return;}

public static void rs_write(RS stn,int n)
{
	switch(n){
		case 0 : 
		{
			    int m=stn.i;		
				add_rs[m].status = "AVAILABLE";
				cdb.q=add_rs[m].t;
		}
	break;
		
		case 1 : 
		{
			    int m=stn.i;		
				mult_rs[m].status = "AVAILABLE";
				cdb.q=mult_rs[m].t;
		}
	break;
		case 2 : 
		{
			    int m=stn.i;		
				ld_rs[m].status = "AVAILABLE";
				cdb.q=ld_rs[m].t;
		}
	break;
}
return;
}


	public static void main(String[] args) throws IOException
	{
		//Taking command line arguments
		String filename=args[0];
		num_add_rs=Integer.parseInt(args[1]);
		num_mult_rs=Integer.parseInt(args[2]);
		num_ld_rs=Integer.parseInt(args[3]);
		num_add_unit=Integer.parseInt(args[4]);
		num_mult_unit=Integer.parseInt(args[5]);
		num_ld_unit=Integer.parseInt(args[6]);
		num_br_unit=Integer.parseInt(args[7]);
		add_latency=Integer.parseInt(args[8]);
		sub_latency=Integer.parseInt(args[9]);
		mult_latency=Integer.parseInt(args[10]);
	    div_latency=Integer.parseInt(args[11]);
		ld_latency=Integer.parseInt(args[12]);
		st_latency=Integer.parseInt(args[13]);
		br_latency=Integer.parseInt(args[14]);
		num_mem_unit=Integer.parseInt(args[15]);
		branch_result=args[16];
		z=1;
	    inst=new ArrayList<String>();
		@SuppressWarnings("resource")
		BufferedReader br1 = new BufferedReader(new FileReader(filename));
	    String line1;
		iq=new Instruction[50];
		int j=0;
		while((line1=br1.readLine())!=null)
		{
			
			String[] tokens=line1.split(" ");
             iq[j]=new Instruction();
			 iq[j].type =tokens[0];
			 iq[j].line =j;
			 if(tokens[0].equals("LW"))
			 {
			 iq[j].dest =tokens[1];
			 iq[j].src1=tokens[2];
			 }
			 else if(tokens[0].equals("SW"))
			 {
				 iq[j].src1=tokens[1];
				 iq[j].src2=tokens[2];
			 }
			 else if(tokens[0].equals("BNEZ"))
			 {
				 br_num++;
				 iq[j].src1=tokens[1];
				 iq[j].src2=tokens[2]; 
				 if("NT".equals(branch_result))
				 {
					 j++;
				num_inst=j;
				number=j;
				 break;
				 }
				 while("T".equals(branch_result))
				 {  
	                 number=j+1;
				     int k=j+1;
					 for(int i=0;i<3;i++)
					 {
						for(int l=0;l<=j;l++)
						 {
						iq[k]=new Instruction();
						iq[k].copy(iq[l]);
						 iq[k].line=k;
						 k++;}
					 }
					 num_inst=k;
					 br_num=4;
					 break;
				 }
				break; 
			 }
			 else
		     {
				 iq[j].dest =tokens[1];
			 iq[j].src1=tokens[2];
			 iq[j].src2=tokens[3];
				 }
			 j++;
			 num_inst=j;
			 number=num_inst;
		}
		
		arch();
		int k=0;

		while(k==0) {

			cycles++;
			writeback();
			memory();
			execute();
			
			if(inst_num<num_inst)
			issue ();
			if(cdb.q!=null)
			{
			if(inst.contains(cdb.q.dest))
			    inst.remove(cdb.q.dest);
				inst.add(cdb.q.dest);
			}
	        int a=0;
			int b=0;int c=0; int d=0;
			int count=0;
			int i;
			for(i=0;i<num_add_rs;i++)
			{
               if(add_rs[i].status=="AVAILABLE")
               count++;
			}
			if(count==num_add_rs)
			a=1;
			count=0;
			for(i=0;i<num_mult_rs;i++)
			{
	           if(mult_rs[i].status=="AVAILABLE")
	        	   count++;
			}
			if(count==num_mult_rs)
			b=1;
			count=0;
			if(cycles==44)
				break;
			for(i=0;i<num_ld_rs;i++)
			{	
	           if(ld_rs[i].status=="AVAILABLE")
			   count++;
			}
			if(count==num_ld_rs)
				c=1;
			count=0;
				for(i=0;i<br_num;i++)
				{      
		           if(br_rs[i].status=="AVAILABLE")
                   count++;
				}
				if(count==br_num)
					d=1;
				if(a==1&&b==1&&c==1&&d==1)
					break;
		}
	System.out.println("Number of cycles taken to run is"+ " " + cycles);
}
	}
