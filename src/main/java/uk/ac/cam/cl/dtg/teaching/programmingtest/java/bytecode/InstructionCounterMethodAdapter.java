/**
 * pottery-container-java - Within-container library for testing Java code
 * Copyright © 2015 Andrew Rice (acr31@cam.ac.uk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.cam.cl.dtg.teaching.programmingtest.java.bytecode;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InstructionCounterMethodAdapter extends MethodVisitor {


	public static final boolean COUNT_BY_OPCODE = true;
	
	//http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-6.html
	private static final int INCREMENT_INSTRUCTION = 0;
	private static final int INCREMENT_ALLOCATION = 1;
	
	private int count = 0;
	
	public InstructionCounterMethodAdapter(MethodVisitor mv) {
		super(Opcodes.ASM5,mv);
	}

	@Override
	public void visitIincInsn(int var, int increment) {
		mv.visitIincInsn(var, increment);
		increment(INCREMENT_INSTRUCTION,Opcodes.IINC);
	}
	
	
	/**
	 * NOP, ACONST_NULL, ICONST_M1, ICONST_0,ICONST_1,ICONST_2,ICONST_3,ICONST_4,ICONST_5,LCONST_0, LCONST_1, FCONST_0,FCONST_1, FCONST_2, DCONST_0, DCONST_1 
	 * IASTORE, LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD,
	 * IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE, POP, POP2, DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, 
	 * SWAP, IADD, LADD, FADD, DADD, ISUB, LSUB, FSUB, DSUB, IMUL, LMUL, FMUL, DMUL, IDIV, LDIV, FDIV, DDIV, IREM, LREM, FREM, DREM, 
	 * INEG, LNEG, FNEG,  DNEG, ISHL, LSHL, ISHR, LSHR, IUSHR, LUSHR, IAND, LAND, IOR, LOR, IXOR, LXOR
	 * I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F, I2B, I2C, I2S, LCMP, FCMPL, FCMPG, DCMPL, DCMPG 
	 * IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN, ARRAYLENGTH, ATHROW, MONITORENTER, MONITOREXIT, IALOAD
	 */
	@Override
	public void visitInsn(int opcode) {
		mv.visitInsn(opcode);
		switch (opcode) {
		case Opcodes.ICONST_0:
		case Opcodes.ICONST_1:
		case Opcodes.ICONST_2:
		case Opcodes.ICONST_3:
		case Opcodes.ICONST_4:
		case Opcodes.ICONST_5:
		case Opcodes.FCONST_0:
		case Opcodes.FCONST_1:
		case Opcodes.FCONST_2:
		case Opcodes.DCONST_0:
		case Opcodes.DCONST_1:
		case Opcodes.LCONST_0:
		case Opcodes.LCONST_1:
		case Opcodes.DUP:
		case Opcodes.DUP_X1:
		case Opcodes.DUP_X2:
		case Opcodes.DUP2:
		case Opcodes.DUP2_X1:
		case Opcodes.DUP2_X2:
		case Opcodes.ARRAYLENGTH:
		case Opcodes.RETURN:
		case Opcodes.IRETURN:
		case Opcodes.LRETURN:
		case Opcodes.FRETURN:
		case Opcodes.DRETURN:
			// don't count these.  They are just loading a constant on the stack so we can just count the operation that uses the constant
			break;
		default:
			increment(INCREMENT_INSTRUCTION,opcode);
		}
	}

	/**
	 * NEWARRAY, BIPUSH, SIPUSH
	 */
	@Override
	public void visitIntInsn(int opcode, int operand) {
		mv.visitIntInsn(opcode, operand);
		switch (opcode) {
		case Opcodes.NEWARRAY:	
			increment(INCREMENT_ALLOCATION,opcode);
			break;
		case Opcodes.BIPUSH:
		case Opcodes.SIPUSH:
			// don't count these.  They are just loading a constant on the stack so we can just count the operation that uses the constant
			break;
		default:
			
			increment(INCREMENT_INSTRUCTION,opcode);
		}
	}

	/**
	 * IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ACMPEQ, IF_ACMPNE
	 * GOTO, JSR, IFNULL, IFNONNULL
	 * 
	 */
	@Override
	public void visitJumpInsn(int opcode, Label label) {
		mv.visitJumpInsn(opcode, label);
		increment(INCREMENT_INSTRUCTION,opcode);
	}
		
	/**
	 * INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC, INVOKEINTERFACE
	 */
	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc, boolean itf) {
		mv.visitMethodInsn(opcode, owner, name, desc, itf);
		if (owner.equals("sun/misc/Unsafe")) {
			throw new InstrumentationUnsafeError();
		}
		if (opcode == Opcodes.INVOKESPECIAL && name.equals("<init>") && waitForInit) {
			waitForInit = false;
			increment(INCREMENT_ALLOCATION,Opcodes.NEW);
		}
	}

	private boolean waitForInit = false;
	
	/**
	 * NEW, ANEWARRAY
	 */
	@Override
	public void visitTypeInsn(int opcode, String type) {
		mv.visitTypeInsn(opcode, type);
		if (opcode == Opcodes.NEW) {
			waitForInit = true;
		}
	}
	
	/**
	 * TABLESWITCH
	 */
	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt,
			Label... labels) {
		mv.visitTableSwitchInsn(min, max, dflt, labels);
		increment(INCREMENT_INSTRUCTION,Opcodes.TABLESWITCH);
	}

	/**
	 * LOOKUPSWITCH
	 */	
	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		mv.visitLookupSwitchInsn(dflt, keys, labels);
		increment(INCREMENT_INSTRUCTION,Opcodes.LOOKUPSWITCH);
	}

	/**
	 * MULTIANEWARRAY
	 */
	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		mv.visitMultiANewArrayInsn(desc, dims);
		increment(INCREMENT_ALLOCATION,Opcodes.MULTIANEWARRAY);
	}
	
	private void increment(int type,int opcode) {
		switch(type) {
		case INCREMENT_ALLOCATION:
			count+=2;
			mv.visitInsn(Opcodes.DUP);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
					"uk/ac/cam/cl/dtg/teaching/counter/Counter", "incrementAllocations", "(Ljava/lang/Object;)V",
					false);
			break;
		default: // case INCREMENT_INSTRUCTION:
			count++;
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
					"uk/ac/cam/cl/dtg/teaching/counter/Counter", "incrementInstructions", "()V",
					false);
		}
		
		if (COUNT_BY_OPCODE) {
			count+=2;
			mv.visitIntInsn(Opcodes.SIPUSH,opcode);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
					"uk/ac/cam/cl/dtg/teaching/counter/Counter", "incrementInstructionsByOpcode", "(I)V",
					false);
		} else {
			
		}
	}
	
	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		mv.visitMaxs(maxStack+count, maxLocals);
	}

	
}
