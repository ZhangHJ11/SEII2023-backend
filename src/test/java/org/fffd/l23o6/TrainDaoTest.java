package org.fffd.l23o6;

import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.service.TrainService;
import org.fffd.l23o6.service.impl.TrainServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TrainDaoTest {

    @Mock
    private TrainDao trainDao;

    @InjectMocks
    private TrainServiceImpl trainService;

    public TrainDaoTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById() {
        // 单元测试 TrainDao的findById方法 准备测试数据
        Long routeId = 1L;
        TrainEntity trains = new TrainEntity();
        // 添加trainEntity到trains列表

        // 模拟trainDao的行为
        when(trainDao.findById(routeId)).thenReturn(Optional.of(trains));

        // 调用trainDao的方法
        TrainEntity result = trainDao.findById(routeId).orElse(null);

        // 验证返回结果是否符合预期
        assertEquals(trains, result);

        // 验证trainDao的方法是否被调用
        verify(trainDao, times(1)).findById(routeId);
    }
}