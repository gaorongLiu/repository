package com.changgou.order.service.impl;

import com.changgou.order.dao.TaskHisMapper;
import com.changgou.order.dao.TaskMapper;
import com.changgou.order.pojo.Task;
import com.changgou.order.pojo.TaskHis;
import com.changgou.order.service.TaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskHisMapper taskHisMapper;
    @Override
    @Transactional
    public void dekTask(Task task) {
        task.setDeleteTime(new Date());
        Long id = task.getId();
        task.setId(null);

        //base拷贝
        TaskHis taskHis=new TaskHis();
        BeanUtils.copyProperties(task,taskHis);

        //记录历史任务数据
taskHisMapper.insertSelective(taskHis);
        //删除原有数据
        task.setId(id);
        taskMapper.deleteByPrimaryKey(task);
        System.out.println("任务完成");
    }
}
