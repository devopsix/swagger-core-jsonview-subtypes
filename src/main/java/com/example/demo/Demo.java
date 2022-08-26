package com.example.demo;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.media.Schema;

import java.lang.annotation.Annotation;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

public class Demo {

	public interface Input {}

	public interface Output {}

	@JsonTypeInfo(use = NAME)
	@JsonSubTypes({
		@JsonSubTypes.Type(value = A1.class),
	})
	public static abstract class A {
		@JsonView(Input.class)
		public String a_in;
		@JsonView(Output.class)
		public String a_out;
	}

	public static class A1 extends A {
		@JsonView(Input.class)
		public String a1_in;
		@JsonView(Output.class)
		public String a1_out;
	}

	private static final JsonView VIEW_OUTPUT = new JsonView() {
		public Class<? extends Annotation> annotationType() {
			return JsonView.class;
		}
		public Class<?>[] value() {
			return new Class[] {Output.class};
		}
	};

	public static void main(String[] args) {
		ResolvedSchema resolvedSchema = ModelConverters.getInstance()
			.resolveAsResolvedSchema(
				new AnnotatedType(A.class).resolveAsRef(true)
					.jsonViewAnnotation(VIEW_OUTPUT));
		resolvedSchema.referencedSchemas.forEach((name, schema) -> dumpSchema(schema, ""));
	}

	private static void dumpSchema(Schema<?> schema, String indent) {
		if (schema.get$ref() != null) {
			System.out.println(indent + "$ref: " + schema.get$ref());
		}
		if (schema.getName() != null) {
			System.out.println(indent + "Schema: " + schema.getName());
		}
		if (schema.getType() != null) {
			System.out.println(indent + "Type: " + schema.getType());
		}
		if (schema.getAllOf() != null) {
			System.out.println(indent + "All of:");
			schema.getAllOf().forEach((allOfSchema) -> dumpSchema(allOfSchema, indent + "  "));
		}
		if (schema.getProperties() != null) {
			System.out.println(indent + "Properties:");
			schema.getProperties().forEach((propName, propSchema) -> {
				System.out.println(indent + propName);
				dumpSchema(propSchema, indent + "  ");
			});
		}
	}
}
