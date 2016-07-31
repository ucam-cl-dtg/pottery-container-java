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

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

public class Counter {

	private static AtomicLong instructions = new AtomicLong(0);
	private static AtomicLong allocations = new AtomicLong(0);
	
	public static boolean ENABLE_INSTRUCTION_COUNTING = false;
	
	public static TreeMap<Integer,AtomicLong> instructionsMap = new TreeMap<Integer,AtomicLong>();
	
	public static void incrementInstructions() {
		instructions.incrementAndGet();
	}
	
	public static long getTotalInstructions() {
		return instructions.get();
	}

	public static void incrementInstructionsByOpcode(int opcode) {
		if (!instructionsMap.containsKey(opcode)) {
			instructionsMap.put(opcode,new AtomicLong(0));
		}
		instructionsMap.get(opcode).incrementAndGet();
	}
	
	public static void incrementAllocations(Object allocated) {
		allocations.addAndGet(Premain.instrumentation.getObjectSize(allocated));
	}
	
	public static long getTotalAllocations() {
		return allocations.get();
	}

	public static void print() {
		System.out.println("Itotal:\t"+instructions.get());
		System.out.println("Atotal:\t"+allocations.get());
		
		@SuppressWarnings("unchecked")
		Map<Integer,AtomicLong> copy = (Map<Integer,AtomicLong>)Counter.instructionsMap.clone();
		for(Integer i : copy.keySet()) {
			System.out.println(OPCODES.get(i)+"\t"+copy.get(i).get());
		}
	}
	
	
	private static Map<Integer,String> OPCODES = new HashMap<Integer,String>();
	static {
		OPCODES.put(0,"NOP");
		OPCODES.put(1,"ACONST_NULL");
		OPCODES.put(2,"ICONST_M1");
		OPCODES.put(3,"ICONST_0");
		OPCODES.put(4,"ICONST_1");
		OPCODES.put(5,"ICONST_2");
		OPCODES.put(6,"ICONST_3");
		OPCODES.put(7,"ICONST_4");
		OPCODES.put(8,"ICONST_5");
		OPCODES.put(9,"LCONST_0");
		OPCODES.put(10,"LCONST_1");
		OPCODES.put(11,"FCONST_0");
		OPCODES.put(12,"FCONST_1");
		OPCODES.put(13,"FCONST_2");
		OPCODES.put(14,"DCONST_0");
		OPCODES.put(15,"DCONST_1");
		OPCODES.put(16,"BIPUSH");
		OPCODES.put(17,"SIPUSH");
		OPCODES.put(18,"LDC");
		OPCODES.put(21,"ILOAD");
		OPCODES.put(22,"LLOAD");
		OPCODES.put(23,"FLOAD");
		OPCODES.put(24,"DLOAD");
		OPCODES.put(25,"ALOAD");
		OPCODES.put(46,"IALOAD");
		OPCODES.put(47,"LALOAD");
		OPCODES.put(48,"FALOAD");
		OPCODES.put(49,"DALOAD");
		OPCODES.put(50,"AALOAD");
		OPCODES.put(51,"BALOAD");
		OPCODES.put(52,"CALOAD");
		OPCODES.put(53,"SALOAD");
		OPCODES.put(54,"ISTORE");
		OPCODES.put(55,"LSTORE");
		OPCODES.put(56,"FSTORE");
		OPCODES.put(57,"DSTORE");
		OPCODES.put(58,"ASTORE");
		OPCODES.put(79,"IASTORE");
		OPCODES.put(80,"LASTORE");
		OPCODES.put(81,"FASTORE");
		OPCODES.put(82,"DASTORE");
		OPCODES.put(83,"AASTORE");
		OPCODES.put(84,"BASTORE");
		OPCODES.put(85,"CASTORE");
		OPCODES.put(86,"SASTORE");
		OPCODES.put(87,"POP");
		OPCODES.put(88,"POP2");
		OPCODES.put(89,"DUP");
		OPCODES.put(90,"DUP_X1");
		OPCODES.put(91,"DUP_X2");
		OPCODES.put(92,"DUP2");
		OPCODES.put(93,"DUP2_X1");
		OPCODES.put(94,"DUP2_X2");
		OPCODES.put(95,"SWAP");
		OPCODES.put(96,"IADD");
		OPCODES.put(97,"LADD");
		OPCODES.put(98,"FADD");
		OPCODES.put(99,"DADD");
		OPCODES.put(100,"ISUB");
		OPCODES.put(101,"LSUB");
		OPCODES.put(102,"FSUB");
		OPCODES.put(103,"DSUB");
		OPCODES.put(104,"IMUL");
		OPCODES.put(105,"LMUL");
		OPCODES.put(106,"FMUL");
		OPCODES.put(107,"DMUL");
		OPCODES.put(108,"IDIV");
		OPCODES.put(109,"LDIV");
		OPCODES.put(110,"FDIV");
		OPCODES.put(111,"DDIV");
		OPCODES.put(112,"IREM");
		OPCODES.put(113,"LREM");
		OPCODES.put(114,"FREM");
		OPCODES.put(115,"DREM");
		OPCODES.put(116,"INEG");
		OPCODES.put(117,"LNEG");
		OPCODES.put(118,"FNEG");
		OPCODES.put(119,"DNEG");
		OPCODES.put(120,"ISHL");
		OPCODES.put(121,"LSHL");
		OPCODES.put(122,"ISHR");
		OPCODES.put(123,"LSHR");
		OPCODES.put(124,"IUSHR");
		OPCODES.put(125,"LUSHR");
		OPCODES.put(126,"IAND");
		OPCODES.put(127,"LAND");
		OPCODES.put(128,"IOR");
		OPCODES.put(129,"LOR");
		OPCODES.put(130,"IXOR");
		OPCODES.put(131,"LXOR");
		OPCODES.put(132,"IINC");
		OPCODES.put(133,"I2L");
		OPCODES.put(134,"I2F");
		OPCODES.put(135,"I2D");
		OPCODES.put(136,"L2I");
		OPCODES.put(137,"L2F");
		OPCODES.put(138,"L2D");
		OPCODES.put(139,"F2I");
		OPCODES.put(140,"F2L");
		OPCODES.put(141,"F2D");
		OPCODES.put(142,"D2I");
		OPCODES.put(143,"D2L");
		OPCODES.put(144,"D2F");
		OPCODES.put(145,"I2B");
		OPCODES.put(146,"I2C");
		OPCODES.put(147,"I2S");
		OPCODES.put(148,"LCMP");
		OPCODES.put(149,"FCMPL");
		OPCODES.put(150,"FCMPG");
		OPCODES.put(151,"DCMPL");
		OPCODES.put(152,"DCMPG");
		OPCODES.put(153,"IFEQ");
		OPCODES.put(154,"IFNE");
		OPCODES.put(155,"IFLT");
		OPCODES.put(156,"IFGE");
		OPCODES.put(157,"IFGT");
		OPCODES.put(158,"IFLE");
		OPCODES.put(159,"IF_ICMPEQ");
		OPCODES.put(160,"IF_ICMPNE");
		OPCODES.put(161,"IF_ICMPLT");
		OPCODES.put(162,"IF_ICMPGE");
		OPCODES.put(163,"IF_ICMPGT");
		OPCODES.put(164,"IF_ICMPLE");
		OPCODES.put(165,"IF_ACMPEQ");
		OPCODES.put(166,"IF_ACMPNE");
		OPCODES.put(167,"GOTO");
		OPCODES.put(168,"JSR");
		OPCODES.put(169,"RET");
		OPCODES.put(170,"TABLESWITCH");
		OPCODES.put(171,"LOOKUPSWITCH");
		OPCODES.put(172,"IRETURN");
		OPCODES.put(173,"LRETURN");
		OPCODES.put(174,"FRETURN");
		OPCODES.put(175,"DRETURN");
		OPCODES.put(176,"ARETURN");
		OPCODES.put(177,"RETURN");
		OPCODES.put(178,"GETSTATIC");
		OPCODES.put(179,"PUTSTATIC");
		OPCODES.put(180,"GETFIELD");
		OPCODES.put(181,"PUTFIELD");
		OPCODES.put(182,"INVOKEVIRTUAL");
		OPCODES.put(183,"INVOKESPECIAL");
		OPCODES.put(184,"INVOKESTATIC");
		OPCODES.put(185,"INVOKEINTERFACE");
		OPCODES.put(186,"INVOKEDYNAMIC");
		OPCODES.put(187,"NEW");
		OPCODES.put(188,"NEWARRAY");
		OPCODES.put(189,"ANEWARRAY");
		OPCODES.put(190,"ARRAYLENGTH");
		OPCODES.put(191,"ATHROW");
		OPCODES.put(192,"CHECKCAST");
		OPCODES.put(193,"INSTANCEOF");
		OPCODES.put(194,"MONITORENTER");
		OPCODES.put(195,"MONITOREXIT");
		OPCODES.put(197,"MULTIANEWARRAY");
		OPCODES.put(198,"IFNULL");
		OPCODES.put(199,"IFNONNULL");
	}

}
