package restx.xml;

import com.fasterxml.jackson.databind.ObjectWriter;
import restx.RestxContext;
import restx.RestxRequest;
import restx.RestxResponse;
import restx.entity.AbstractEntityResponseWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public class XmlEntityResponseWriter<T> extends AbstractEntityResponseWriter<T> {

    public static <T> XmlEntityResponseWriter<T> using(String contentType, Type type, ObjectWriter writer) {
        return new XmlEntityResponseWriter<>(contentType, type, writer);
    }

    protected final ObjectWriter writer;

    protected XmlEntityResponseWriter(String contentType, Type type, ObjectWriter writer) {
        super(type, contentType);
        this.writer = writer;
    }

    @Override
    protected void write(T value, RestxRequest req, RestxResponse resp, RestxContext ctx) throws IOException {
        writer.writeValue(resp.getWriter(), value);
    }
}
