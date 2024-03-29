package kr.co.broadwave.desk.notice;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


/**
 * @author InSeok
 * Date : 2019-07-29
 * Remark :
 */
@Slf4j
@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final NoticeRepositoryCustom noticeRepositoryCustom;
    private final ModelMapper modelMapper;

    @Autowired
    public NoticeService(NoticeRepository noticeRepository, NoticeRepositoryCustom noticeRepositoryCustom, ModelMapper modelMapper) {
        this.noticeRepository = noticeRepository;
        this.noticeRepositoryCustom = noticeRepositoryCustom;
        this.modelMapper = modelMapper;
    }

    public Notice save(Notice notice){

        return noticeRepository.save(notice);
    }

    public NoticeDto findById(Long id){
        Optional<Notice> optionalNotice = noticeRepository.findById(id);
        if (optionalNotice.isPresent()) {
            return modelMapper.map(optionalNotice.get(), NoticeDto.class);
        } else {
            return null;
        }
    }
    public Optional<Notice> findByIdNotice(Long id){
        return  noticeRepository.findById(id);

    }

    public Page<NoticeDto> findAllBySearchStrings(String username, String subject, Pageable pageable){
        return noticeRepositoryCustom.findAllBySearchStrings(username,subject,pageable);

    }
    public void delete(Notice notice){
        noticeRepository.delete(notice);
    }
    //공지사항 클릭수 증가
    public Long hitCountUpdate(Long noticeId){
        Optional<Notice> optionalNotice = noticeRepository.findById(noticeId);
        if (optionalNotice.isPresent()) {
            return noticeRepositoryCustom.hitCountUpdate(optionalNotice.get());
        }else{
            return -1L;
        }

    }

    public List<NoticeIdStateDto> findByIdState() {
        return noticeRepositoryCustom.findByIdState();
    }

}
