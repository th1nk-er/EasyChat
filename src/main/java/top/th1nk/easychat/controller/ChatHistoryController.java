package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conversation")
@Tag(name = "用户对话模块", description = "用户对话API")
public class ChatHistoryController {

}
