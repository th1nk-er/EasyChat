package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.th1nk.easychat.domain.SysChatMessage;
import top.th1nk.easychat.service.SysChatMessageService;
import top.th1nk.easychat.mapper.SysChatMessageMapper;
import org.springframework.stereotype.Service;

/**
* @author vinka
* @description 针对表【ec_chat_message】的数据库操作Service实现
* @createDate 2024-08-01 18:08:50
*/
@Service
public class SysChatMessageServiceImpl extends ServiceImpl<SysChatMessageMapper, SysChatMessage>
    implements SysChatMessageService{

}




