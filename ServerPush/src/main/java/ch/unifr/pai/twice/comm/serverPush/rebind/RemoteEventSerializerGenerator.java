package ch.unifr.pai.twice.comm.serverPush.rebind;

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

import ch.unifr.pai.twice.authentication.client.security.MessagingException;
import ch.unifr.pai.twice.authentication.client.security.TWICESecurityManager;
import ch.unifr.pai.twice.comm.serverPush.client.RemoteEvent;
import ch.unifr.pai.twice.comm.serverPush.client.RemoteEventWrapper;
import ch.unifr.pai.twice.comm.serverPush.client.UndoableRemoteEvent;
import ch.unifr.pai.twice.comm.serverPush.client.UndoableRemoteEventWrapper;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generator logic for remote event serialization. Generation takes place at compile time
 * 
 * @author Oliver Schmid
 * 
 */
public class RemoteEventSerializerGenerator extends Generator {

	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		// Build a new class, that implements a "paintScreen" method
		JClassType classType;

		try {
			classType = context.getTypeOracle().getType(typeName);
			JClassType superClass = classType.getSuperclass();
			JClassType[] generics = superClass.isParameterized().getTypeArgs();
			JClassType eventHandlerClass = generics[0];

			// Here you would retrieve the metadata based on typeName for this
			// Screen
			SourceWriter src = getSourceWriter(classType, context, logger);
			if (src != null) {
				src.println("@Override");
				src.println("public " + String.class.getName() + " getEventType(){");
				src.println("return " + classType.getQualifiedSourceName() + ".class.getName();");
				src.println("}");

				if (superClass.getQualifiedSourceName().equals(RemoteEventWrapper.class.getName())
						|| superClass.getQualifiedSourceName().equals(UndoableRemoteEventWrapper.class.getName())) {
					JClassType eventClass = generics[1];
					src.println("@Override");
					src.println("public void wrap(" + eventClass.getQualifiedSourceName() + " event){");
					for (JMethod method : classType.getMethods()) {
						String realMethodName = method.getName().replaceAll("_", "().");
						src.println("setProperty(\"" + method.getName() + "\", " + String.class.getName() + ".valueOf(event." + realMethodName + "()));");
					}
					src.println("}");

					for (JMethod method : classType.getMethods()) {
						if (method.isAbstract()) {
							src.println();
							src.println("@Override");
							src.println("public " + String.class.getName() + " " + method.getName() + "(){");
							src.println(JSONValue.class.getName() + " value = json.get(\"" + method.getName() + "\");");
							src.println("return value!=null && value.isString()!=null ? value.isString().stringValue() : null;");
							src.println("}");
						}
					}

					src.println();
				}

				src.println("@Override");
				src.println("public " + GwtEvent.class.getName() + "." + Type.class.getSimpleName() + "<" + eventHandlerClass.getQualifiedSourceName()
						+ "> getAssociatedType() {");
				src.println("\treturn " + classType.getQualifiedSourceName() + ".TYPE;");
				src.println("}");

				src.println();

				src.println("@Override");
				src.println("protected void dispatch(" + eventHandlerClass.getQualifiedSourceName() + " handler) {");
				// for (JMethod m : eventHandlerClass.getMethods()) {
				// if(!m.getName().equals("undo")){
				boolean undoable = classType.isAssignableTo(context.getTypeOracle().getType(UndoableRemoteEvent.class.getName()));
				if (undoable) {
					src.println("if(isUndo())");
					src.println("handler.undo(this);");
					src.println("else {");
					src.println("handler.saveState(this);");
				}
				src.println("\t handler.onEvent(this);");
				if (undoable) {
					src.println("}");
				}

				// }
				// }
				src.println("}");
				src.println();
				src.println("@Override");
				src.println("public String serialize(" + TWICESecurityManager.class.getName() + " security) throws " + MessagingException.class.getName() + "{");
				for (JField field : classType.getFields()) {
					if (!field.isStatic() && !field.isTransient()) {
						src.println("if(" + field.getName() + "!=null){");
						src.println("setProperty(\"" + field.getName() + "\", String.valueOf(" + field.getName() + "));}");

					}
				}
				src.println("return super.serialize(security);");
				src.println("}");
				src.println();
				src.println("@Override");
				src.println("public " + RemoteEvent.class.getName() + "<" + eventHandlerClass.getQualifiedSourceName() + "> deserialize(String string, "
						+ TWICESecurityManager.class.getName() + " security) throws " + MessagingException.class.getName() + "{");
				src.println(RemoteEvent.class.getName() + " result = super.deserialize(string, security);");
				for (JField field : classType.getFields()) {
					if (!field.isStatic()) {
						if (String.class.getName().equals(field.getType().getQualifiedSourceName()))
							src.println(field.getName() + " = getProperty(\"" + field.getName() + "\");");
						else {
							src.println("String " + field.getName() + "Tmp = getProperty(\"" + field.getName() + "\");");
							src.println(field.getName() + " = " + field.getName() + "Tmp!=null ? " + field.getType().getQualifiedSourceName() + ".valueOf("
									+ field.getName() + "Tmp) : null;");
						}
					}
				}
				src.println("return result;");
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

	public SourceWriter getSourceWriter(JClassType classType, GeneratorContext context, TreeLogger logger) {
		String packageName = classType.getPackage().getName();
		String simpleName = classType.getSimpleSourceName() + "Impl";
		ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);
		composer.setSuperclass(classType.getName());

		composer.addImport(classType.getQualifiedSourceName());

		// Need to add whatever imports your generated class needs.
		// composer.addImport("com.google.gwt.user.client.Window");
		// composer.addImport("com.google.gwt.user.client.rpc.AsyncCallback");
		// composer.addImport("com.google.gwt.user.client.ui.Button");
		// composer.addImport("com.google.gwt.user.client.ui.RootPanel");

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
