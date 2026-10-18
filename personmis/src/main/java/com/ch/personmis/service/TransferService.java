package com.ch.personmis.service;

import com.ch.personmis.entity.Transfer;

import java.util.Map;

/**
 * 岗位调动服务接口
 *
 * 功能说明：
 * 定义调动前岗位查询、调动新增、调动记录分页/条件查询等能力。
 */
public interface TransferService {
    public Transfer getBeforePost(int id);
    public  String addTransfer(Transfer transfer);
    public Map<String, Object> getTransfer(Transfer transfer);
    public  Map<String, Object> selectTransfersByCon(Transfer transfer);
}
