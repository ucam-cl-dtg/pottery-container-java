package uk.ac.cam.cl.dtg.teaching.programmingtest.java;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Joiner;

public class Accessor {

	private Harness harness;

	public Accessor(Harness harness) {
		this.harness = harness;
	}

	private void addMethodList(Class<?> c, Set<Method> result) {
		if (c == null)
			return;
		result.addAll(Arrays.asList(c.getDeclaredMethods()));
		addMethodList(c.getSuperclass(), result);
	}

	private void addFieldList(Class<?> c, Set<Field> result) {
		if (c == null)
			return;
		result.addAll(Arrays.asList(c.getDeclaredFields()));
		addFieldList(c.getSuperclass(), result);
	}

	public Object invokeMain(String className, String... params)
			throws Throwable {
		if (params == null)
			params = new String[] {};
		return methodInvoker(loadClass(className), null, "main",
				new Object[] { params });
	}

	public Object invoke(String className, String methodName, Object... params)
			throws Throwable {
		return methodInvoker(loadClass(className), null, methodName, params);
	}

	public Object invoke(Object o, String methodName, Object... params)
			throws Throwable {
		return methodInvoker(o.getClass(), o, methodName, params);
	}

	private static boolean primitiveMatch(Class<?> primitive, Class<?> boxed,
			Object param) {
		return param.getClass().equals(primitive) || boxed.isInstance(param);
	}

	private static boolean paramMatch(Class<?> clazz, Object param) {
		if (clazz.isPrimitive()) {
			if (clazz.equals(Integer.TYPE)) {
				return primitiveMatch(Integer.TYPE, Integer.class, param);
			} else if (clazz.equals(Boolean.TYPE)) {
				return primitiveMatch(Boolean.TYPE, Boolean.class, param);
			} else if (clazz.equals(Float.TYPE)) {
				return primitiveMatch(Float.TYPE, Float.class, param);
			} else if (clazz.equals(Double.TYPE)) {
				return primitiveMatch(Double.TYPE, Double.class, param);
			} else if (clazz.equals(Character.TYPE)) {
				return primitiveMatch(Character.TYPE, Character.class, param);
			} else if (clazz.equals(Long.TYPE)) {
				return primitiveMatch(Long.TYPE, Long.class, param);
			} else if (clazz.equals(Short.TYPE)) {
				return primitiveMatch(Short.TYPE, Short.class, param);
			} else {
				throw new Error("Unrecognised primitive type "
						+ clazz.getName());
			}
		} else {
			return clazz.isInstance(param);
		}
	}

	private Object methodInvoker(Class<?> c, Object o, String methodName,
			Object... params) throws Throwable {
		Set<Method> methods = new HashSet<Method>();
		addMethodList(c, methods);
		for (Method m : methods) {
			if (methodName.equals(m.getName())) {
				Class<?>[] paramTypes = m.getParameterTypes();
				if (paramTypes.length == params.length) {
					boolean match = true;
					for (int i = 0; i < paramTypes.length; ++i) {
						if (params[i] != null
								&& !paramMatch(paramTypes[i], params[i])) {
							match = false;
						}
					}

					if (match)
						try {
							m.setAccessible(true);
							if (!Modifier.isStatic(m.getModifiers()) && o == null) {
								throw new RuntimeException("Method "+m+" is not static");
							}
							harness.log("Called "+m.toString());
							return m.invoke(o, params);
						} catch (InvocationTargetException ex) {
							throw ex.getTargetException();
						}
				}
			}
		}
		if (o == null) {
			throw new NoSuchMethodException("Static method " + methodName
					+ " not found in class " + c.getName());
		} else {
			throw new NoSuchMethodException("Method " + methodName
					+ " not found in class " + c.getName());
		}
	}

	public Class<?> loadClass(String className) throws Throwable {
		return getClass().getClassLoader().loadClass(className);
	}

	public Object construct(String className, Object... params)
			throws Throwable {
		Class<?> clazz = loadClass(className);
		for (Constructor<?> c : clazz.getDeclaredConstructors()) {
			Class<?>[] paramTypes = c.getParameterTypes();
			if (paramTypes.length == params.length) {
				boolean match = true;
				for (int i = 0; i < paramTypes.length; ++i) {
					if (params[i] != null
							&& !paramMatch(paramTypes[i], params[i])) {
						match = false;
					}
				}
				if (match) {
					c.setAccessible(true);
					try {
						return c.newInstance(params);
					} catch (InvocationTargetException e) {
						throw e.getTargetException();
					}
				}
			}
		}

		String[] argTypes = new String[params.length];
		for (int i = 0; i < params.length; ++i) {
			argTypes[i] = params[i] == null ? "(null)" : params[i].getClass()
					.getSimpleName();
		}
		throw new NoSuchMethodException(String.format("Constructor %s(%s) not found",className,Joiner.on(",").join(argTypes)));
	}

	public Object getField(Object instance, String field) throws Throwable {
		Class<?> c = instance.getClass();
		Set<Field> fields = new HashSet<Field>();
		addFieldList(c, fields);
		for (Field f : fields) {
			if (f.getName().equals(field)) {
				f.setAccessible(true);
				return f.get(instance);
			}
		}
		throw new NoSuchFieldException("Field " + field + " not found in class " + c.getName());
	}
}
