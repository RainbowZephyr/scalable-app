package services;

import io.netty.channel.ChannelHandler;

import java.util.Map;


public interface RequestServer extends ChannelHandler {
    ServiceRequest constructReq(Map<String, Object> request);
}
