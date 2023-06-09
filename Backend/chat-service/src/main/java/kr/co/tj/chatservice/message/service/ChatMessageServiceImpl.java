package kr.co.tj.chatservice.message.service;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import kr.co.tj.chatservice.message.dto.ChatMessageDTO;
import kr.co.tj.chatservice.message.dto.ChatMessageResponse;
import kr.co.tj.chatservice.message.persistence.ChatMessageEntity;
import kr.co.tj.chatservice.message.persistence.ChatMessageRepository;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
	
	private SimpMessageSendingOperations messagingTemplate;
	private ChatMessageRepository chatMessageRepository;
	
	@Autowired
	public ChatMessageServiceImpl(
			SimpMessageSendingOperations messagingTemplate,
			ChatMessageRepository chatMessageRepository) {
		
		super();
		this.messagingTemplate = messagingTemplate;
		this.chatMessageRepository = chatMessageRepository;
	}


	
	@Override
	@Transactional
	public void saveAndSendMessage(ChatMessageDTO dto) {
		
		Date date = new Date();
		
		ChatMessageEntity entity = ChatMessageEntity.builder()
				.roomTitle(dto.getRoomTitle())
				.sendAt(date)
				.sender(dto.getSender())
				.receiver(dto.getReceiver())
				.message(dto.getMessage())
				.build();
		
		entity = chatMessageRepository.save(entity);
		
		ChatMessageResponse response = ChatMessageResponse.builder()
				.roomTitle(entity.getRoomTitle())
				.sendAt(entity.getSendAt())
				.sender(entity.getSender())
				.receiver(entity.getReceiver())
				.message(entity.getMessage())
				.build();
	
	messagingTemplate.convertAndSend("/sub/chatroom/" + response.getRoomTitle(), response);
	
		
	}
	
}
