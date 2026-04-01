class processPacketIn {
private net.floodlightcontroller.core.IListener.Command processPacketIn(IOFSwitch sw, OFPacketIn pi, FloodlightContext cntx) {
        
        Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
        IPacket pkt = eth.getPayload(); 
 
        if (eth.isBroadcast() || eth.isMulticast()) {
            // handle ARP for VIP
            if (pkt instanceof ARP) {
                // retrieve arp to determine target IP address                                                       
                ARP arpRequest = (ARP) eth.getPayload();

                IPv4Address targetProtocolAddress = arpRequest.getTargetProtocolAddress();

                if (vipIpToId.containsKey(targetProtocolAddress.getInt())) {
                    String vipId = vipIpToId.get(targetProtocolAddress.getInt());
                    vipProxyArpReply(sw, pi, cntx, vipId);
                    return Command.STOP;
                }
            }
        } else {
            // currently only load balance IPv4 packets - no-op for other traffic 
            if (pkt instanceof IPv4) {
                IPv4 ip_pkt = (IPv4) pkt;
                
                // If match Vip and port, check pool and choose member
                int destIpAddress = ip_pkt.getDestinationAddress().getInt();
                
                if (vipIpToId.containsKey(destIpAddress)){
                    IPClient client = new IPClient();
                    client.ipAddress = ip_pkt.getSourceAddress();
                    client.nw_proto = ip_pkt.getProtocol();
                    if (ip_pkt.getPayload() instanceof TCP) {
                        TCP tcp_pkt = (TCP) ip_pkt.getPayload();
                        client.srcPort = tcp_pkt.getSourcePort();
                        client.targetPort = tcp_pkt.getDestinationPort();
                    }
                    if (ip_pkt.getPayload() instanceof UDP) {
                        UDP udp_pkt = (UDP) ip_pkt.getPayload();
                        client.srcPort = udp_pkt.getSourcePort();
                        client.targetPort = udp_pkt.getDestinationPort();
                    }
                    if (ip_pkt.getPayload() instanceof ICMP) {
                        client.srcPort = TransportPort.of(8); 
                        client.targetPort = TransportPort.of(0); 
                    }
                    
                    LBVip vip = vips.get(vipIpToId.get(destIpAddress));
                    LBPool pool = pools.get(vip.pickPool(client));
                    LBMember member = members.get(pool.pickMember(client));

                    // for chosen member, check device manager and find and push routes, in both directions                    
                    pushBidirectionalVipRoutes(sw, pi, cntx, client, member);
                   
                    // packet out based on table rule
                    pushPacket(pkt, sw, pi.getBufferId(), (pi.getVersion().compareTo(OFVersion.OF_12) < 0) ? pi.getInPort() : pi.getMatch().get(MatchField.IN_PORT), OFPort.TABLE,
                                cntx, true);

                    return Command.STOP;
                }
            }
        }
        // bypass non-load-balanced traffic for normal processing (forwarding)
        return Command.CONTINUE;
    }
}
