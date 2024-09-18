package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.attr.AttrValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomDetailVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.awt.print.PrinterAbortException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private AttrValueMapper attrValueMapper;


    @Autowired
    private RoomAttrValueService attrValueService;

    @Autowired
    private RoomFacilityService facilityService;

    @Autowired
    private RoomLabelService labelService;

    @Autowired
    private RoomPaymentTypeService paymentTypeService;

    @Autowired
    private RoomLeaseTermService leaseTermService;

    @Autowired
    private GraphInfoService graphInfoService;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Autowired
    private RoomAttrValueService roomAttrValueService;

    @Autowired
    private RoomFacilityService roomFacilityService;

    @Autowired
    private RoomLabelService roomLabelService;

    @Autowired
    private RoomPaymentTypeService roomPaymentTypeService;

    @Autowired
    private RoomLeaseTermService roomLeaseTermService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void saveOrUpdateRoom(RoomSubmitVo roomSubmitVo) {
        boolean isUpdated = roomSubmitVo.getId() != null;

        super.saveOrUpdate(roomSubmitVo);

        //有房间号：更新操作，没有房间号：新增操作
        //更新操作：将原数据删除，在新增数据
        //新增操作：直接新增新数据
        if (isUpdated) {
            //1.删除房间属性
            LambdaQueryWrapper<RoomAttrValue> attrValueQueryWrapper = new LambdaQueryWrapper<>();
            attrValueQueryWrapper.eq(RoomAttrValue::getRoomId, roomSubmitVo.getId());
            attrValueService.remove(attrValueQueryWrapper);

            //2.删除房间配套
            LambdaQueryWrapper<RoomFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
            facilityQueryWrapper.eq(RoomFacility::getRoomId, roomSubmitVo.getId());
            facilityService.remove(facilityQueryWrapper);

            //3.删除房间标签
            LambdaQueryWrapper<RoomLabel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(RoomLabel::getRoomId, roomSubmitVo.getId());
            labelService.remove(lambdaQueryWrapper);

            //4.删除支付方式
            LambdaQueryWrapper<RoomPaymentType> paymentTypeQueryWrapper = new LambdaQueryWrapper<>();
            paymentTypeQueryWrapper.eq(RoomPaymentType::getRoomId, roomSubmitVo.getId());
            paymentTypeService.remove(paymentTypeQueryWrapper);

            //5.删除可选租期
            LambdaQueryWrapper<RoomLeaseTerm> leaseTermQueryWrapper = new LambdaQueryWrapper<>();
            leaseTermQueryWrapper.eq(RoomLeaseTerm::getRoomId, roomSubmitVo.getId());
            leaseTermService.remove(leaseTermQueryWrapper);

            //6.删除图片列表
            LambdaQueryWrapper<GraphInfo> graphInfoQueryWrapper = new LambdaQueryWrapper<>();
            graphInfoQueryWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
            graphInfoQueryWrapper.eq(GraphInfo::getItemId, roomSubmitVo.getId());
            graphInfoService.remove(graphInfoQueryWrapper);

            //7.删除缓存
            redisTemplate.delete(RedisConstant.APP_ROOM_PREFIX + roomSubmitVo.getId());
        }

        //1.新增房间属性
        List<Long> attrValueIds = roomSubmitVo.getAttrValueIds();
        if (!CollectionUtils.isEmpty(attrValueIds)) {
            List<RoomAttrValue> attrValues = new ArrayList<>();
            for (Long attrValueId : attrValueIds) {
                RoomAttrValue roomAttrValue = RoomAttrValue
                        .builder()
                        .roomId(roomSubmitVo.getId())
                        .attrValueId(attrValueId)
                        .build();
                attrValues.add(roomAttrValue);
            }
        }

        //2.新增房间配套
        List<Long> facilityIds = roomSubmitVo.getFacilityInfoIds();
        if (!CollectionUtils.isEmpty(facilityIds)) {
            List<RoomFacility> roomFacilities = new ArrayList<>();
            for (Long facilityId : facilityIds) {
                RoomFacility roomFacility = RoomFacility.builder()
                        .roomId(roomSubmitVo.getId())
                        .facilityId(facilityId)
                        .build();
                roomFacilities.add(roomFacility);
            }
        }

        //3.新增房间标签
        List<Long> labelIds = roomSubmitVo.getLabelInfoIds();
        if (!CollectionUtils.isEmpty(labelIds)) {
            List<RoomLabel> roomLabels = new ArrayList<>();
            for (Long labelId : labelIds) {
                RoomLabel roomLabel = RoomLabel.builder()
                        .roomId(roomSubmitVo.getId())
                        .labelId(labelId)
                        .build();
                roomLabels.add(roomLabel);
            }
        }

        //4.新增支付方式
        List<Long> paymentTypeIds = roomSubmitVo.getPaymentTypeIds();
        if (!CollectionUtils.isEmpty(paymentTypeIds)) {
            List<RoomPaymentType> roomPaymentTypes = new ArrayList<>();
            for (Long paymentTypeId : paymentTypeIds) {
                RoomPaymentType roomPaymentType = RoomPaymentType.builder()
                        .roomId(roomSubmitVo.getId())
                        .paymentTypeId(paymentTypeId)
                        .build();
                roomPaymentTypes.add(roomPaymentType);
            }
        }

        //5.新增可选租期
        List<Long> leaseTermIds = roomSubmitVo.getLeaseTermIds();
        if (!CollectionUtils.isEmpty(leaseTermIds)) {
            List<RoomLeaseTerm> roomLeaseTerms = new ArrayList<>();
            for (Long leaseTermId : leaseTermIds) {
                RoomLeaseTerm roomLeaseTerm = RoomLeaseTerm.builder()
                        .roomId(roomSubmitVo.getId())
                        .leaseTermId(leaseTermId)
                        .build();
                roomLeaseTerms.add(roomLeaseTerm);
            }
        }

        //6.新增图片列表
        List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
        if (!CollectionUtils.isEmpty(graphVoList)) {
            List<GraphInfo> graphInfos = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setItemId(roomSubmitVo.getId());
                graphInfo.setItemType(ItemType.ROOM);
                graphInfo.setUrl(graphVo.getUrl());
                graphInfos.add(graphInfo);
            }
        }
    }

    @Override
    public IPage<RoomItemVo> pageRoomItemByQuery(Page<RoomItemVo> page, RoomQueryVo queryVo) {
        return roomInfoMapper.pageRoomItemByQuery(page, queryVo);
    }

    @Override
    public RoomDetailVo getRoomDetailById(Long id) {

        //1.查询RoomInfo
        RoomInfo roomInfo = roomInfoMapper.selectById(id);

        //2.查询所属公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(roomInfo.getApartmentId());

        //3.查询graphInfoList
        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, id);

        //4.查询attrValueList
        List<AttrValueVo> attrvalueVoList = attrValueMapper.selectListByRoomId(id);

        //5.查询facilityInfoList
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByRoomId(id);

        //6.查询labelInfoList
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByRoomId(id);

        //7.查询paymentTypeList
        List<PaymentType> paymentTypeList = paymentTypeMapper.selectListByRoomId(id);

        //8.查询leaseTermList
        List<LeaseTerm> leaseTermList = leaseTermMapper.selectListByRoomId(id);

        RoomDetailVo adminRoomDetailVo = new RoomDetailVo();
        BeanUtils.copyProperties(roomInfo, adminRoomDetailVo);

        adminRoomDetailVo.setApartmentInfo(apartmentInfo);
        adminRoomDetailVo.setGraphVoList(graphVoList);
        adminRoomDetailVo.setAttrValueVoList(attrvalueVoList);
        adminRoomDetailVo.setFacilityInfoList(facilityInfoList);
        adminRoomDetailVo.setLabelInfoList(labelInfoList);
        adminRoomDetailVo.setPaymentTypeList(paymentTypeList);
        adminRoomDetailVo.setLeaseTermList(leaseTermList);

        return adminRoomDetailVo;
    }

    @Override
    public void removeRoomById(Long id) {
        //1.删除RoomInfo
        super.removeById(id);

        //2.删除graphInfoList
        LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
        graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
        graphQueryWrapper.eq(GraphInfo::getItemId, id);
        graphInfoService.remove(graphQueryWrapper);

        //3.删除attrValueList
        LambdaQueryWrapper<RoomAttrValue> attrQueryWrapper = new LambdaQueryWrapper<>();
        attrQueryWrapper.eq(RoomAttrValue::getRoomId, id);
        roomAttrValueService.remove(attrQueryWrapper);

        //4.删除facilityInfoList
        LambdaQueryWrapper<RoomFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
        facilityQueryWrapper.eq(RoomFacility::getRoomId, id);
        roomFacilityService.remove(facilityQueryWrapper);

        //5.删除labelInfoList
        LambdaQueryWrapper<RoomLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
        labelQueryWrapper.eq(RoomLabel::getRoomId, id);
        roomLabelService.remove(labelQueryWrapper);

        //6.删除paymentTypeList
        LambdaQueryWrapper<RoomPaymentType> paymentQueryWrapper = new LambdaQueryWrapper<>();
        paymentQueryWrapper.eq(RoomPaymentType::getRoomId, id);
        roomPaymentTypeService.remove(paymentQueryWrapper);

        //7.删除leaseTermList
        LambdaQueryWrapper<RoomLeaseTerm> termQueryWrapper = new LambdaQueryWrapper<>();
        termQueryWrapper.eq(RoomLeaseTerm::getRoomId, id);
        roomLeaseTermService.remove(termQueryWrapper);

        //8.删除缓存
        redisTemplate.delete(RedisConstant.APP_ROOM_PREFIX + id);
    }
}




