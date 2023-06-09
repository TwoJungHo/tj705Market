package kr.co.tj.chatservice.message.controller;




import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.MessageMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import io.jsonwebtoken.Jwts;
import kr.co.tj.chatservice.message.dto.ChatMessageDTO;
import kr.co.tj.chatservice.message.dto.ChatMessageRequest;

import kr.co.tj.chatservice.message.service.ChatMessageService;






@Controller
@RequestMapping("/chat-service")
public class ChatMessageController {
	
	private Environment env;
	private ChatMessageService chatMessageService;
	
	@Autowired
	public ChatMessageController(Environment env, ChatMessageService chatMessageService) {
		super();
		this.env = env;
		this.chatMessageService = chatMessageService;
	}






	@MessageMapping("/sendmessage")
	public void message(ChatMessageRequest request) {
		
		String token = request.getBearertoken().replace("Bearer ", "");
		String secKey = env.getProperty("data.SECRET_KEY");
		String encodedSecKey = Base64.getEncoder().encodeToString(secKey.getBytes());
		String sender = Jwts.parser()
				.setSigningKey(encodedSecKey)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		
		ChatMessageDTO dto = ChatMessageDTO.builder()
				.roomTitle(request.getRoomTitle())
				.sender(sender)
				.receiver(request.getReceiver())
				.message(request.getMessage())
				.build();
		
		chatMessageService.saveAndSendMessage(dto);
		
	}

}
