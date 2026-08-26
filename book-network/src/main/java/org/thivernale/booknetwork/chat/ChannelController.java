package org.thivernale.booknetwork.chat;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
@Tag(name = "Channel")
public class ChannelController {
    private final ChannelService channelService;

    @PostMapping(produces = "application/json")
    ResponseEntity<Long> createChannel(
        @RequestParam("sender-id") long senderId, @RequestParam("recipient-id") long recipientId) {
        return ResponseEntity
            .ok(channelService.create(senderId, recipientId));
    }

    @GetMapping
    ResponseEntity<List<ChannelResponse>> getChannels(Authentication authentication) {
        return ResponseEntity
            .ok(channelService.findByUser(authentication));
    }
}
