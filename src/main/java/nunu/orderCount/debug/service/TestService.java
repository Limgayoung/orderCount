package nunu.orderCount.debug.service;

import lombok.RequiredArgsConstructor;
import nunu.orderCount.debug.controller.exception.TestException;
import nunu.orderCount.debug.model.TestEntity;
import nunu.orderCount.debug.repository.TestRepository;
import nunu.orderCount.global.error.ErrorCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;

    public TestEntity saveEntity(String name){
        return testRepository.save(TestEntity.builder()
                .name(name).build());
    }

    public TestEntity getEntity(Long id){
        return testRepository.findById(id).orElseThrow(() -> new TestException(ErrorCode.NOT_FOUND_DATA, "해당하는 test entity를 찾을 수 없습니다"));
    }

}
