/*
 * Copyright (C) 20012 The Pekall Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.launcher2.theme;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflector {

	public static Object newInstance(String className, Class[] classTypes,
			Object[] classArgs) throws ClassNotFoundException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Class clazz = Class.forName(className);
		Constructor constructor = clazz.getDeclaredConstructor(classTypes);
		constructor.setAccessible(true);
		return constructor.newInstance(classArgs);
	}

	public static Object newInstance(String className)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Class clazz = Class.forName(className);
		return clazz.newInstance();
	}

	public static Object invokeMethod(Object instance, String methodName,
			Class[] methodTypes, Object[] methodArgs)
			throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Method accessMethod = getMethod(instance.getClass(), methodName,
				methodTypes);
		accessMethod.setAccessible(true);
		return accessMethod.invoke(instance, methodArgs);
	}

	public static Object invokeMethod(Object instance, String methodName,
			Object[] methodArgs) throws NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Method accessMethod = getMethod(instance.getClass(), methodName);
		accessMethod.setAccessible(true);
		return accessMethod.invoke(instance, methodArgs);
	}

	public static Object invokeStaticMethod(Class clazz, String methodName,
			Class[] methodTypes, Object[] methodArgs)
			throws NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Method accessMethod = getMethod(clazz, methodName, methodTypes);
		accessMethod.setAccessible(true);
		return accessMethod.invoke(null, methodArgs);
	}

	public static void setValue(Object instance, String fieldName, Object value)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field field = getField(instance.getClass(), fieldName);
		field.setAccessible(true);
		field.set(instance, value);
	}

	public static Object getValue(Object instance, String fieldName)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field field = getField(instance.getClass(), fieldName);
		field.setAccessible(true);
		return field.get(instance);
	}

	public static Object getStaticValue(Class clazz, String fieldName)
			throws SecurityException, NoSuchFieldException,
			IllegalArgumentException, IllegalAccessException {
		Field field = getField(clazz, fieldName);
		field.setAccessible(true);
		return field.get(null);
	}

	private static Method getMethod(Class clazz, String methodName,
			Class[] classTypes) throws NoSuchMethodException {

		if (clazz == null) {
			throw new NoSuchMethodException(" No such method ! ");
		}

		try {
			Method accessMethod = clazz.getDeclaredMethod(methodName,
					classTypes);
			accessMethod.setAccessible(true);
			return accessMethod;
		} catch (NoSuchMethodException e) {
			return getMethod(clazz.getSuperclass(), methodName, classTypes);
		}
	}

	private static Method getMethod(Class clazz, String methodName)
			throws NoSuchMethodException {

		if (clazz == null) {
			throw new NoSuchMethodException(" No such method ! ");
		}

		try {
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					method.setAccessible(true);
					return method;
				}
			}
			return getMethod(clazz.getSuperclass(), methodName);
		} catch (NoSuchMethodException e) {
			return getMethod(clazz.getSuperclass(), methodName);
		}
	}

	private static Field getField(Class clazz, String fieldName)
			throws NoSuchFieldException {

		if (clazz == null) {
			throw new NoSuchFieldException(" No such method ! ");
		}

		try {
			Field accessField = clazz.getDeclaredField(fieldName);
			accessField.setAccessible(true);
			return accessField;
		} catch (NoSuchFieldException e) {
			return getField(clazz.getSuperclass(), fieldName);
		}
	}

}
