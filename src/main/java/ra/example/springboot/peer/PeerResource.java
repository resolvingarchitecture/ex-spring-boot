/*
  This is free and unencumbered software released into the public domain.

  Anyone is free to copy, modify, publish, use, compile, sell, or
  distribute this software, either in source code form or as a compiled
  binary, for any purpose, commercial or non-commercial, and by any
  means.

  In jurisdictions that recognize copyright laws, the author or authors
  of this software dedicate any and all copyright interest in the
  software to the public domain. We make this dedication for the benefit
  of the public at large and to the detriment of our heirs and
  successors. We intend this dedication to be an overt act of
  relinquishment in perpetuity of all present and future rights to this
  software under copyright law.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
  IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
  OTHER DEALINGS IN THE SOFTWARE.

  For more information, please refer to <http://unlicense.org/>
 */
package ra.example.springboot.peer;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "peers")
@Path("/peers")
public class PeerResource {

    private static Map<Integer, Peer> DB = new HashMap<>();

    @GET
    @Produces("application/json")
    public Peers getAllPeers() {
        Peers peers = new Peers();
        peers.peers = new ArrayList<>(DB.values());
        return peers;
    }

    @POST
    @Consumes("application/json")
    public Response createPeer(Peer peer) throws URISyntaxException
    {
        if(peer.firstName == null || peer.lastName == null) {
            return Response.status(400).entity("Please provide all required inputs").build();
        }
        peer.id = DB.values().size()+1;
        peer.uri = "/peers/"+peer.id;
        DB.put(peer.id, peer);
        return Response.status(201).contentLocation(new URI(peer.uri)).build();
    }

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Response getPeerById(@PathParam("id") int id) throws URISyntaxException
    {
        Peer peer = DB.get(id);
        if(peer == null) {
            return Response.status(404).build();
        }
        return Response
                .status(200)
                .entity(peer)
                .contentLocation(new URI("/peers/"+id)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes("application/json")
    @Produces("application/json")
    public Response updatePeer(@PathParam("id") int id, Peer peer) throws URISyntaxException
    {
        Peer temp = DB.get(id);
        if(peer == null) {
            return Response.status(404).build();
        }
        temp.firstName = peer.firstName;
        temp.lastName = peer.lastName;
        DB.put(temp.id, temp);
        return Response.status(200).entity(temp).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePeer(@PathParam("id") int id) throws URISyntaxException {
        Peer peer = DB.get(id);
        if(peer != null) {
            DB.remove(peer.id);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    static
    {
        Peer peer1 = new Peer();
        peer1.id = 1;
        peer1.firstName = "Alice";
        peer1.lastName = "Gray";
        peer1.uri = "/peers/1";
        DB.put(peer1.id, peer1);

        Peer peer2 = new Peer();
        peer2.id = 2;
        peer2.firstName = "Bob";
        peer2.lastName = "Gray";
        peer2.uri = "/peers/2";

        DB.put(peer2.id, peer2);
    }
}
