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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generator logic for the remote event deserializer. Generation takes place at compile time.
 * 
 * @author Oliver Schmid
 * 
 */
public class RemoteEventDeSerializerGenerator extends Generator {

	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
		// Build a new class, that implements a "paintScreen" method
		JClassType classType;

		try {
			classType = context.getTypeOracle().getType(typeName);
			// Here you would retrieve the metadata based on typeName for this
			// Screen
			SourceWriter src = getSourceWriter(classType, context, logger);
			if (src != null) {
				src.println("@Override");
				src.println("public " + RemoteEvent.class.getName() + "<?> deserialize(" + JSONObject.class.getName() + " o, String t, String string, "
						+ TWICESecurityManager.class.getName() + " securityManager) throws " + MessagingException.class.getName() + " {");
				JClassType abstractRemoteEvent = context.getTypeOracle().findType(RemoteEvent.class.getName());
				src.println("if(t==null){");
				src.println("return null;");
				src.println("}");
				for (JClassType subType : abstractRemoteEvent.getSubtypes()) {
					if (!subType.getPackage().getName()
							.contains(ch.unifr.pai.twice.comm.serverPush.client.RemoteEventDeserializer.class.getPackage().getName())
							&& !subType.getName().endsWith("Impl")) {
						src.println("else if(t.equals(" + subType.getQualifiedSourceName() + ".class.getName())){");
						src.println(subType.getQualifiedSourceName() + " event = " + GWT.class.getName() + ".create(" + subType.getQualifiedSourceName()
								+ ".class);");
						src.println("return event.deserialize(string, securityManager);");
						src.println("}");
					}
				}
				src.println("return null;}");
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
