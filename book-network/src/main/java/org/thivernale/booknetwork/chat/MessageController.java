package org.thivernale.booknetwork.chat;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Tag(name = "Message")
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void createMessage(@RequestBody MessageRequest messageRequest) {
        messageService.create(messageRequest);
    }

    @PostMapping(path = "/upload-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    void createMediaMessage(
        @Parameter(required = true, description = "The file to upload")
        @RequestPart("file") MultipartFile file,
        @RequestParam("channel-id") Long channelId,
        Authentication authentication) {
        messageService.uploadMediaMessage(channelId, file, authentication);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    void getChannels(@RequestParam("channel-id") Long channelId, Authentication authentication) {
        messageService.markAsRead(channelId, authentication);
    }

    @GetMapping("/channel/{channel-id}")
    ResponseEntity<List<MessageResponse>> getMessages(@PathVariable("channel-id") Long channelId) {
        return ResponseEntity.ok(messageService.findChannelMessages(channelId));
    }
}
