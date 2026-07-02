package com.spin.transaction_executor.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;

public class ResetteableStreamHttpServlet extends HttpServletRequestWrapper {
    private byte[] rawData;

    private HttpServletRequest request;

    private ResettableServletInputStream servletStream;

    public ResetteableStreamHttpServlet(HttpServletRequest request) {
        super(request);
        this.request = request;
        this.servletStream = new ResettableServletInputStream();
    }

    public void resetInputStream() {
        this.servletStream.stream = new ByteArrayInputStream(this.rawData);
    }

    public ServletInputStream getInputStream() throws IOException {
        if (this.rawData == null) {
            this.rawData = IOUtils.toByteArray(this.request.getReader(),"UTF-8");
            this.servletStream.stream = new ByteArrayInputStream(this.rawData);
        }
        return this.servletStream;
    }

    public BufferedReader getReader() throws IOException {
        if (this.rawData == null) {
            this.rawData = IOUtils.toByteArray(this.request.getReader(),"UTF-8");
            this.servletStream.stream = new ByteArrayInputStream(this.rawData);
        }
        return new BufferedReader(new InputStreamReader((InputStream)this.servletStream));
    }

    private class ResettableServletInputStream extends ServletInputStream {
        private InputStream stream;

        private ResettableServletInputStream() {}

        public int read() throws IOException {
            return this.stream.read();
        }

        public boolean isFinished() {
            return false;
        }

        public boolean isReady() {
            return false;
        }

        public void setReadListener(ReadListener readListener) {}
    }
}
