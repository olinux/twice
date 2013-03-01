package ch.unifr.pai.twice.module.rebind;

/*
 * Copyright 2013 Oliver Schmid
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import ch.unifr.pai.twice.module.client.TWICEAnnotations.Configurable;
import ch.unifr.pai.twice.module.client.TWICEModule;
import ch.unifr.pai.twice.module.client.TWICEModuleInstantiator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JPrimitiveType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generator logic applied at GWT compile time to create the instantiators for modules to establish the lazy loading mechanism
 * 
 * @author Oliver Schmid
 * 
 */
public class TWICEModuleGenerator extends Generator {

	/**
	 * @param classType
	 * @return the class type of the actual component widget provided through the generic annotation
	 */
	private JClassType getGenericClass(JClassType classType) {
		JClassType clazz = null;
		for (JClassType intf : classType.getImplementedInterfaces()) {
			if (intf.getQualifiedSourceName().equals(TWICEModule.class.getName())) {
				JClassType[] generics = intf.isParameterized().getTypeArgs();
				// The twice module has only one generic
				for (JClassType g : generics)
					clazz = g;
			}
		}
		return clazz;
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gwt.core.ext.Generator#generate(com.google.gwt.core.ext.TreeLogger, com.google.gwt.core.ext.GeneratorContext, java.lang.String)
	 */
	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		// Build a new class, that implements a "paintScreen" method
		JClassType classType;
		try {
			classType = context.getTypeOracle().getType(typeName);
			JClassType genericClass = getGenericClass(classType);

			SourceWriter src = getSourceWriter(classType, context, logger);
			if (src != null) {
				src.println("@Override");
				src.println("public " + Map.class.getName() + "<" + String.class.getName() + ", " + Object.class.getName() + "> getConfigurableFields("
						+ genericClass.getQualifiedSourceName() + " instance){");
				src.println(Map.class.getName() + "<" + String.class.getName() + ", " + Object.class.getName() + "> result = new " + HashMap.class.getName()
						+ "<" + String.class.getName() + ", " + Object.class.getName() + ">();");
				for (JField f : genericClass.getFields()) {
					Configurable c = f.getAnnotation(Configurable.class);
					if (c != null && !f.isFinal() && !f.isPrivate() && !f.isProtected()) {
						src.println("result.put(\"" + c.value() + "\", instance." + f.getName() + ");");
					}
				}
				src.println("return result;");
				src.println("}");

				src.println("@Override");
				src.println("public void configure(" + Map.class.getName() + "<" + String.class.getName() + ", " + String.class.getName() + "> properties, "
						+ genericClass.getQualifiedSourceName() + " instance){");
				src.println("for(" + String.class.getName() + " key : properties.keySet()){");
				src.println("String value = properties.get(key);");
				src.println("if(key==null){");
				src.println("}");
				for (JField f : genericClass.getFields()) {
					Configurable c = f.getAnnotation(Configurable.class);
					if (c != null && !f.isFinal() && !f.isPrivate() && !f.isProtected()) {
						JPrimitiveType t = f.getType().isPrimitive();
						if (t != null) {
							src.println("else if(key.equals(\"" + c.value() + "\")){");
							switch (t) {
								case INT:
									src.println("instance." + f.getName() + "=" + Integer.class.getName() + ".parseInt(value);");
									break;
								case BOOLEAN:
									src.println("instance." + f.getName() + "=" + Boolean.class.getName() + ".parseBoolean(value);");
									break;
								case DOUBLE:
									src.println("instance." + f.getName() + "=" + Double.class.getName() + ".parseDouble(value);");
									break;
								case FLOAT:
									src.println("instance." + f.getName() + "=" + Float.class.getName() + ".parseFloat(value);");
									break;
								case LONG:
									src.println("instance." + f.getName() + "=" + Long.class.getName() + ".parseLong(value);");
									break;
								default:
									throw new RuntimeException("The primitive type \"" + t.name() + "\" is not supported for configuration");
							}
						}
						else if (f.getType().getQualifiedSourceName().equals(String.class.getName())) {
							src.println("instance." + f.getName() + "=value");
						}
						else {
							throw new RuntimeException("The type \"" + f.getType().getQualifiedSourceName() + "\" is not supported for configuration");
						}
						src.println("}");
					}
				}
				src.println("}");
				src.println("}");

				src.println("@Override");
				src.println("public " + RunAsyncCallback.class.getName() + " instantiate(final " + AsyncCallback.class.getName() + "<"
						+ genericClass.getQualifiedSourceName() + "> callback){");
				src.println("return new " + RunAsyncCallback.class.getName() + "(){");
				src.println("@Override");
				src.println("public void onSuccess(){");
				src.println(genericClass.getQualifiedSourceName() + " module = " + GWT.class.getName() + ".create(" + genericClass.getQualifiedSourceName()
						+ ".class);");
				src.println("//start(module);");
				src.println("callback.onSuccess(module);");
				src.println("}");
				src.println("@Override");
				src.println("public void onFailure(" + Throwable.class.getName() + " reason){");
				src.println("callback.onFailure(reason);");
				src.println("}");
				src.println("};");
				src.println("}");
				src.commit(logger);
			}

			return typeName + "Impl";

		}
		catch (NotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Define the class to be generated.
	 * 
	 * @param classType
	 * @param context
	 * @param logger
	 * @return
	 */
	public SourceWriter getSourceWriter(JClassType classType, GeneratorContext context, TreeLogger logger) {
		String packageName = classType.getPackage().getName();
		String simpleName = classType.getSimpleSourceName() + "Impl";
		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);
		composer.setSuperclass(classType.getName());
		composer.addImplementedInterface(TWICEModuleInstantiator.class.getName() + "<" + getGenericClass(classType).getQualifiedSourceName() + ">");

		PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
		if (printWriter == null) {
			return null;
		}
		else {
			SourceWriter sw = composer.createSourceWriter(context, printWriter);
			return sw;
		}
	}

}
