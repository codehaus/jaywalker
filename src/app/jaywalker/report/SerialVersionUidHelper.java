/**
 * Copyright 2005 ThoughtWorks, Inc.
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
package jaywalker.report;

import jaywalker.classlist.ClassElement;
import jaywalker.classlist.ClasslistElementFactory;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.FieldOrMethod;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class SerialVersionUidHelper {
	private Map isUrlSerializableMap = new HashMap();

	private Map isClassNameSerializableMap = new HashMap();

	private Map urlToSerialVersionUIDMap = new HashMap();

	private String SERIALIZABLE_YES = "YES";

	private String SERIALIZABLE_NO = "NO";

	private String SERIALIZABLE_PENDING = "PENDING";

	private ClasslistElementFactory factory = new ClasslistElementFactory();

	private final static Comparator COMPARATOR_FIELD_OR_METHOD = new FieldOrMethodComparator();

	private final Map classNameToUrlsMap;

	public SerialVersionUidHelper(Map classNameToUrlsMap) {
		this.classNameToUrlsMap = classNameToUrlsMap;
	}

	private static class FieldOrMethodComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}
			FieldOrMethod fieldOrMethod1 = (FieldOrMethod) o1;
			FieldOrMethod fieldOrMethod2 = (FieldOrMethod) o2;
			int compareValue = fieldOrMethod1.getName().compareTo(
					fieldOrMethod2.getName());
			if (compareValue == 0) {
				compareValue = fieldOrMethod1.getSignature().compareTo(
						fieldOrMethod2.getSignature());
			}
			return compareValue;
		}
	}

	public boolean isSerializable(URL url) {
		try {
			String isSerializable = (String) isUrlSerializableMap.get(url);
			if (isSerializable != null)
				return toBoolean(isSerializable);

			isUrlSerializableMap.put(url, SERIALIZABLE_PENDING);
			ClassElement classElement = (ClassElement) factory.create(url);
			return isSerializable(classElement);
		} catch (IllegalStateException e) {
			System.out.println("huh?" + url);
			throw e;
		}

	}

	private boolean isSerializable(ClassElement classElement) {
		String className = classElement.getName();

		if (Object.class.getName().equals(className)) {
			flagAsNotSerializable(classElement);
			return false;
		}

		if (isSerializableImplemented(classElement)) {
			flagAsSerializable(classElement);
			return true;
		}

		boolean retValue = isSuperSerializable(classElement);

		if (retValue) {
			flagAsSerializable(classElement);
		} else {
			flagAsNotSerializable(classElement);
		}

		return retValue;
	}

	private void flagAsNotSerializable(ClassElement classElement) {
		URL url = classElement.getURL();
		isUrlSerializableMap.put(url, SERIALIZABLE_NO);
	}

	private void flagAsSerializable(ClassElement classElement) {
		URL url = classElement.getURL();
		isUrlSerializableMap.put(url, SERIALIZABLE_YES);
		Long serialVersionUid = lookupSerialVersionUID(classElement);
		urlToSerialVersionUIDMap.put(url, serialVersionUid);
	}

	private boolean isSuperSerializable(ClassElement classElement) {
		String superclassName = classElement.getSuperName();
		URL[] superclassUrls = (URL[]) classNameToUrlsMap.get(superclassName);

		if (superclassUrls == null || superclassUrls.length == 0) {
			return isSerializable(superclassName);
		} else {
			// If class resolves to multiple super classes, then find at least
			// one that is serializable
			// This is acceptable since the serialVersionUid is not calculated
			// using the super's state
			for (int i = 0; i < superclassUrls.length; i++) {
				if (isSerializable(superclassUrls[i]))
					return true;
			}
		}
		return false;
	}

	private boolean isSerializableImplemented(ClassElement classElement) {
		String[] interfaceNames = classElement.getInterfaceNames();

		for (int i = 0; i < interfaceNames.length; i++) {
			if (java.io.Serializable.class.getName().equals(interfaceNames[i])) {
				return true;
			}
		}
		return false;
	}

	private Long lookupSerialVersionUID(ClassElement classElement) {
		final Field[] fields = classElement.getJavaClass().getFields();

		for (int i = 0; i < fields.length; i++) {
			// TODO : what if super implements serialVersionUID field
			if ("serialVersionUID".equals(fields[i].getName())) {
				return Long.valueOf(fields[i].getConstantValue().toString());
			}
		}

		return new Long(computeSerialVersionUID(classElement));
	}

	private boolean isSerializable(String className) {
		String isSerializable = (String) isClassNameSerializableMap
				.get(className);
		if (isSerializable != null)
			return toBoolean(isSerializable);
		isClassNameSerializableMap.put(className, SERIALIZABLE_PENDING);

		JavaClass javaClass = Repository.lookupClass(className);
		return isSerializable(javaClass);
	}

	private boolean isSerializable(JavaClass javaClass) {

		String className = javaClass.getClassName();

		if (java.lang.Object.class.getName().equals(className)) {
			flagAsNotSerializable(javaClass);
			return false;
		}

		if (isSerializableImplemented(javaClass)) {
			flagAsSerializable(javaClass);
			return true;
		}

		boolean retValue = isSuperSerializable(javaClass);

		if (retValue) {
			flagAsSerializable(javaClass);
		} else {
			flagAsNotSerializable(javaClass);
		}

		return retValue;

	}

	private void flagAsNotSerializable(JavaClass javaClass) {
		isClassNameSerializableMap.put(javaClass.getClassName(),
				SERIALIZABLE_NO);
	}

	private void flagAsSerializable(JavaClass javaClass) {
		isClassNameSerializableMap.put(javaClass.getClassName(),
				SERIALIZABLE_YES);
	}

	private boolean isSuperSerializable(JavaClass javaClass) {
		String superclassName = javaClass.getSuperclassName();
		URL[] urls = (URL[]) classNameToUrlsMap.get(superclassName);
		if (urls != null) {
			for (int i = 0; i < urls.length; i++) {
				if (isSerializable(urls[i]))
					return true;
			}
			return false;
		} else {
			return isSerializable(superclassName);
		}
	}

	private boolean toBoolean(final String serializable) {
		if (serializable == SERIALIZABLE_PENDING) {
			throw new IllegalStateException(
					"Should not be in a pending state after isSerializable method call.");
		} else {
			return (serializable == SERIALIZABLE_YES);
		}
	}

	private long computeSerialVersionUID(ClassElement classElement) {
		try {
			return computeSerialVersionUID(classElement.getJavaClass());
		} catch (IOException e) {
			return 0;
		} catch (NoSuchAlgorithmException e) {
			return 0;
		}
	}

	public long toSerialVersionUID(URL url) {
		if (url == null)
			throw new IllegalArgumentException("Unexpected URL given");
		Long value = (Long) urlToSerialVersionUIDMap.get(url);
		return value.longValue();
	}

	private boolean isSerializableImplemented(JavaClass javaClass) {
		String[] interfaceNames = javaClass.getInterfaceNames();

		for (int i = 0; i < interfaceNames.length; i++) {
			if (java.io.Serializable.class.getName().equals(interfaceNames[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * http://java.sun.com/j2se/1.4.2/docs/guide/serialization/spec/serialTOC.html
	 * http://java.sun.com/j2se/1.4.2/docs/guide/serialization/spec/class.html
	 * java.io.ObjectStreamClass#computeDefaultSUID()
	 */
	private long computeSerialVersionUID(JavaClass javaClass)
			throws IOException, NoSuchAlgorithmException {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(
				byteArrayOutputStream);

		Method[] methods = javaClass.getMethods();

		// 1. The class name written using UTF encoding.
		dataOutputStream.writeUTF(javaClass.getClassName());

		// 2. The class modifiers written as a 32-bit integer.
		int classModifiers = javaClass.getAccessFlags();

		classModifiers &= (Modifier.PUBLIC | Modifier.FINAL
				| Modifier.INTERFACE | Modifier.ABSTRACT);

		// compensate for javac bug in which ABSTRACT bit was set for an
		// interface only if the interface declared methods
		if (javaClass.isInterface()) {
			classModifiers = (methods.length > 0) ? (classModifiers | Modifier.ABSTRACT)
					: (classModifiers & ~Modifier.ABSTRACT);
		}

		dataOutputStream.writeInt(classModifiers);

		// 3. The name of each interface sorted by name written using UTF
		// encoding.
		String[] interfaces = javaClass.getInterfaceNames();
		Arrays.sort(interfaces);

		for (int i = 0; i < interfaces.length; i++) {
			dataOutputStream.writeUTF(interfaces[i]);
		}

		// 4. For each field of the class sorted by field name (except private
		// static and private transient fields):
		// a. The name of the field in UTF encoding.
		// b. The modifiers of the field written as a 32-bit integer.
		// c. The descriptor of the field in UTF encoding
		Field[] fields = javaClass.getFields();
		Arrays.sort(fields, COMPARATOR_FIELD_OR_METHOD);

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			int fieldModifiers = field.getModifiers();
			if (((fieldModifiers & Modifier.PRIVATE) == 0)
					|| ((fieldModifiers & (Modifier.STATIC | Modifier.TRANSIENT)) == 0)) {
				dataOutputStream.writeUTF(field.getName());
				dataOutputStream.writeInt(fieldModifiers);
				dataOutputStream.writeUTF(field.getSignature());
			}
		}

		// 5. If a class initializer exists, write out the following:
		// a. The name of the method, <clinit>, in UTF encoding.
		// b. The modifier of the method, java.lang.reflect.Modifier.STATIC,
		// written as a 32-bit integer.
		// c. The descriptor of the method, ()V, in UTF encoding.
		// 6. For each non-private constructor sorted by method name and
		// signature:
		// a. The name of the method, <init>, in UTF encoding.
		// b. The modifiers of the method written as a 32-bit integer.
		// c. The descriptor of the method in UTF encoding.
		// 7. For each non-private method sorted by method name and signature:
		// a. The name of the method in UTF encoding.
		// b. The modifiers of the method written as a 32-bit integer.
		// c. The descriptor of the method in UTF encoding.
		Arrays.sort(methods, COMPARATOR_FIELD_OR_METHOD);

		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			int methodModifiers = method.getModifiers();
			if (!method.isPrivate()) {
				dataOutputStream.writeUTF(method.getName());
				dataOutputStream.writeInt(methodModifiers);
				dataOutputStream.writeUTF(method.getSignature().replace('/',
						'.'));
			}
		}

		// 8. The SHA-1 algorithm is executed on the stream of bytes produced by
		// DataOutputStream and produces five 32-bit values sha[0..4].
		dataOutputStream.flush();
		MessageDigest messageDigest = MessageDigest.getInstance("SHA");
		byte[] sha = messageDigest.digest(byteArrayOutputStream.toByteArray());

		// 9. The hash toString is assembled from the first and second 32-bit
		// values of the SHA-1 message digest.
		// If the result of the message digest, the five 32-bit words H0 H1 H2
		// H3 H4, is in an array of five int
		// values named sha, the hash toString would be computed as follows:
		// long hash =
		// ((sha[0] >>> 24) & 0xFF) |
		// ((sha[0] >>> 16) & 0xFF) << 8 |
		// ((sha[0] >>> 8) & 0xFF) << 16 |
		// ((sha[0] >>> 0) & 0xFF) << 24 |
		// ((sha[1] >>> 24) & 0xFF) << 32 |
		// ((sha[1] >>> 16) & 0xFF) << 40 |
		// ((sha[1] >>> 8) & 0xFF) << 48 |
		// ((sha[1] >>> 0) & 0xFF) << 56;
		long hash = 0L;
		for (int i = Math.min(sha.length, 8) - 1; i >= 0; i--) {
			hash = (hash << 8) | (sha[i] & 0xFF);
		}

		return hash;
	}

	public boolean isSerialVersionUidsConflicting(URL[] urls) {
		boolean isConflict = false;
		long serialVersionUid = 0;

		if (urls.length > 0) {
			if (!isSerializable(urls[0]))
				return false;
			serialVersionUid = toSerialVersionUID(urls[0]);
		} else {
			return false;
		}

		for (int i = 1; i < urls.length; i++) {
			if (!isSerializable(urls[i]))
				return false;
			if (toSerialVersionUID(urls[i]) != serialVersionUid) {
				isConflict = true;
			}
		}
		return isConflict;
	}

}
