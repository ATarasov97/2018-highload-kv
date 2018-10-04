package ru.mail.polis.impl;

import java.io.IOException;
import java.util.NoSuchElementException;

import one.nio.http.HttpServer;
import one.nio.http.HttpServerConfig;
import one.nio.http.HttpSession;
import one.nio.http.Param;
import one.nio.http.Path;
import one.nio.http.Request;
import one.nio.http.Response;
import ru.mail.polis.KVDao;
import ru.mail.polis.KVService;

public class KVServiceImpl extends HttpServer implements KVService {

    private final KVDao dao;

    public KVServiceImpl(HttpServerConfig config, KVDao dao) throws IOException {
        super(config);
        this.dao = dao;
    }

    @Path("/v0/entity")
    public Response entity(Request request, HttpSession session,
            @Param(value = "id") String id) throws IOException {
        if (id == null || id.isEmpty()) {
            return new Response(Response.BAD_REQUEST, Response.EMPTY);
        }
        switch (request.getMethod()) {
        case Request.METHOD_GET:
            try {
                byte[] response = dao.get(id.getBytes());
                return new Response(Response.OK, response);
            }
            catch (NoSuchElementException e) {
                return new Response(Response.NOT_FOUND, Response.EMPTY);
            }
        case Request.METHOD_PUT:
            dao.upsert(id.getBytes(), request.getBody());
            return new Response(Response.CREATED, Response.EMPTY);
        case Request.METHOD_DELETE:
            dao.remove(id.getBytes());
            return new Response(Response.ACCEPTED, Response.EMPTY);
        default:
            return new Response(Response.BAD_REQUEST, Response.EMPTY);
        }
    }

    @Path("/v0/status")
    public Response status() {
        return Response.ok(Response.EMPTY);
    }
}
