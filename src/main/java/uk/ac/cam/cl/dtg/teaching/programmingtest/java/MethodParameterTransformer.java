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
package uk.ac.cam.cl.dtg.teaching.programmingtest.java;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodParameterTransformer implements ClassFileTransformer {

	private class TrackMethodVisitor extends MethodVisitor {

		public TrackMethodVisitor(MethodVisitor mv)  {
			super(Opcodes.ASM5,mv);
		}		

		@Override
		public void visitCode() {
			super.visitCode();
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKESTATIC,
					Type.getInternalName(method.getDeclaringClass()),
					method.getName(),
					Type.getMethodDescriptor(method), false);
			mv.visitVarInsn(Opcodes.ASTORE, 0);
			mv.visitCode();
		};
	}

	private Method method;
	private String instrumentClass;
	private String instrumentMethod;
	
	public MethodParameterTransformer(String instrumentClass, String instrumentMethod, Method m) {
		this.instrumentClass = instrumentClass;
		this.instrumentMethod = instrumentMethod;
		this.method = m;
	}
	
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (className.replaceAll("/", ".").equals(instrumentClass)) {
			ClassWriter classWriter = new ClassWriter(Opcodes.ASM5);
			ClassReader classReader = new ClassReader(classfileBuffer);
			ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5,classWriter) {
				@Override
				public MethodVisitor visitMethod(int access, String name, String desc,
						String signature, String[] exceptions) {
					MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
					if (!name.equals(instrumentMethod)) return mv;
					if (mv != null) {
						return new TrackMethodVisitor(mv);
					}
					return null;
				}
			};
			classReader.accept(classVisitor, 0);
			byte[] result = classWriter.toByteArray();
			try {
				try(FileOutputStream fos = new FileOutputStream("/tmp/out.class")) {
					fos.write(result);
				}
			} catch (FileNotFoundException e) {throw new Error(e);
			} catch (IOException e) { throw new Error(e);
			}
			return result;
		}
		return classfileBuffer;
	}

}
