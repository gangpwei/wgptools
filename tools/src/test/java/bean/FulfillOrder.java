package bean;

import java.math.BigDecimal;
import java.util.Date;

//import com.wdk.fulfill.common.bean.Printable;
//import com.wdk.fulfill.common.enums.order.BatchStrategyEnum;
//import com.wdk.fulfill.common.enums.order.FulfillOrderTypeEnum;

/**
 * @author gangpeng.wgp
 * @date 2019/12/25 3:09 PM
 */
public class FulfillOrder  {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6247191321821330474L;

    private Long id;

    /**
     * 履约主单ID 1位实体类型+1位数据版本+8位Sequence+6位日期+2位分库分表位
     */
    private String fulfillOrderId;

    /**
     * 店仓作业计划开始时间
     */
    public Date warehousePlanStartTime;

    /**
     * 店仓作业计划结束时间
     */
    public Date warehousePlanEndTime;

    /**
     * 配作业计划开始时间
     */
    public Date deliveryDockPlanStartTime;

    /**
     * 配作业计划结束时间
     */
    public Date deliveryDockPlanEndTime;

    /**
     * B2C冷链二次分拣计划任务开始时间
     */
    public Date secondPickPlanStartTime;

    /**
     * B2C冷链二次分拣计划任务结束时间
     */
    public Date secondPickPlanEndTime;

    /**
     * mall商家code
     */
    public String relMerchantCode;

    /**
     * mall门店code
     */
    public String relShopCode;

    /**
     * mall渠道店code
     */
    public String relChannelShopCode;

    private String cp;

    /**
     * 履约服务-交易带下来的
     */
    private String fulfillTagFromTrade;

    /**
     * 业务场景
     */
    private String sceneCode;

    /**
     * 餐饮场景作业预计完成时间
     */
    private Date planTime;

    ///**
    // * 类型
    // */
    //private FulfillOrderTypeEnum type;

    /**
     * 盒马经营店ID
     */
    private String shopCode;

    /**
     * 盒马渠道店
     */
    private String channelSourceId;

    /**
     * 交易子业务类型
     */
    private Integer tradeSubBusinessType;

    /**
     * 立刻履约标识
     */
    private String isHold;

    /**
     * 用户选择送达时间
     */
    private String userExpectTime;

    /**
     * 最晚送达时间
     */
    private Date latestArriveTime;

    /**
     * 接单时间
     */
    private Date accepteTime;

    /**
     * 状态
     */
    private String status;

    /**
     * 是否测试数据
     */
    private String isTest;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 修改者
     */
    private String modifier;

    /**
     * 仓Code
     */
    private String warehouseCode;

    /**
     * 配Code
     */
    private String deliveryDockCode;

    /**
     * 配送区块ID
     */
    private String routeArea;

    /**
     * 是否需要配送
     */
    private String needDispatch;
    //
    ///**
    // * 集单策略
    // */
    //private BatchStrategyEnum batchStrategy;

    /**
     * 批次ID
     */
    private String fulfillBatchId;

    /**
     * 商户Code
     */
    private String merchantCode;

    /**
     * 区块名称
     */
    private String routeAreaName;

    /**
     * 截单时间
     */
    private Date batchDeadlineTime;

    /**
     * 盒马渠道店来源
     */
    private Integer channelSourceType;

    /**
     * 交易业务类型
     */
    private Integer tradeBusinessType;

    /**
     * 数据版本
     */
    private Integer ver;

    /**
     * 销售单位数量
     */
    private BigDecimal expectSaleQuantity;

    public Integer getVer() {return ver;}

    public void setVer(Integer ver) {this.ver = ver;}

    public String getFulfillOrderId() {
        return fulfillOrderId;
    }

    public void setFulfillOrderId(String fulfillOrderId) {
        this.fulfillOrderId = fulfillOrderId;
    }

    //public FulfillOrderTypeEnum getType() {
    //    return type;
    //}
    //
    //public void setType(FulfillOrderTypeEnum type) {
    //    this.type = type;
    //}

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getChannelSourceId() {
        return channelSourceId;
    }

    public void setChannelSourceId(String channelSourceId) {
        this.channelSourceId = channelSourceId;
    }

    public String getUserExpectTime() {
        return userExpectTime;
    }

    public void setUserExpectTime(String userExpectTime) {
        this.userExpectTime = userExpectTime;
    }

    public Date getLatestArriveTime() {
        return latestArriveTime;
    }

    public void setLatestArriveTime(Date latestArriveTime) {
        this.latestArriveTime = latestArriveTime;
    }

    public Date getAccepteTime() {
        return accepteTime;
    }

    public void setAccepteTime(Date accepteTime) {
        this.accepteTime = accepteTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsTest() {
        return isTest;
    }

    public void setIsTest(String isTest) {
        this.isTest = isTest;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getDeliveryDockCode() {
        return deliveryDockCode;
    }

    public void setDeliveryDockCode(String deliveryDockCode) {
        this.deliveryDockCode = deliveryDockCode;
    }

    public String getRouteArea() {
        return routeArea;
    }

    public void setRouteArea(String routeArea) {
        this.routeArea = routeArea;
    }

    public String getNeedDispatch() {
        return needDispatch;
    }

    public void setNeedDispatch(String needDispatch) {
        this.needDispatch = needDispatch;
    }
    //
    //public BatchStrategyEnum getBatchStrategy() {
    //    return batchStrategy;
    //}
    //
    //public void setBatchStrategy(BatchStrategyEnum batchStrategy) {
    //    this.batchStrategy = batchStrategy;
    //}

    public String getFulfillBatchId() {
        return fulfillBatchId;
    }

    public void setFulfillBatchId(String fulfillBatchId) {
        this.fulfillBatchId = fulfillBatchId;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getRouteAreaName() {
        return routeAreaName;
    }

    public void setRouteAreaName(String routeAreaName) {
        this.routeAreaName = routeAreaName;
    }

    public Date getBatchDeadlineTime() {
        return batchDeadlineTime;
    }

    public void setBatchDeadlineTime(Date batchDeadlineTime) {
        this.batchDeadlineTime = batchDeadlineTime;
    }

    public Integer getChannelSourceType() {
        return channelSourceType;
    }

    public void setChannelSourceType(Integer channelSourceType) {
        this.channelSourceType = channelSourceType;
    }

    public Integer getTradeBusinessType() {
        return tradeBusinessType;
    }

    public void setTradeBusinessType(Integer tradeBusinessType) {
        this.tradeBusinessType = tradeBusinessType;
    }

    public Integer getTradeSubBusinessType() {
        return tradeSubBusinessType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getWarehousePlanStartTime() {
        return warehousePlanStartTime;
    }

    public void setWarehousePlanStartTime(Date warehousePlanStartTime) {
        this.warehousePlanStartTime = warehousePlanStartTime;
    }

    public Date getWarehousePlanEndTime() {
        return warehousePlanEndTime;
    }

    public void setWarehousePlanEndTime(Date warehousePlanEndTime) {
        this.warehousePlanEndTime = warehousePlanEndTime;
    }

    public Date getDeliveryDockPlanStartTime() {
        return deliveryDockPlanStartTime;
    }

    public void setDeliveryDockPlanStartTime(Date deliveryDockPlanStartTime) {
        this.deliveryDockPlanStartTime = deliveryDockPlanStartTime;
    }

    public Date getDeliveryDockPlanEndTime() {
        return deliveryDockPlanEndTime;
    }

    public void setDeliveryDockPlanEndTime(Date deliveryDockPlanEndTime) {
        this.deliveryDockPlanEndTime = deliveryDockPlanEndTime;
    }

    public Date getSecondPickPlanStartTime() {
        return secondPickPlanStartTime;
    }

    public void setSecondPickPlanStartTime(Date secondPickPlanStartTime) {
        this.secondPickPlanStartTime = secondPickPlanStartTime;
    }

    public Date getSecondPickPlanEndTime() {
        return secondPickPlanEndTime;
    }

    public void setSecondPickPlanEndTime(Date secondPickPlanEndTime) {
        this.secondPickPlanEndTime = secondPickPlanEndTime;
    }

    public String getRelMerchantCode() {
        return relMerchantCode;
    }

    public void setRelMerchantCode(String relMerchantCode) {
        this.relMerchantCode = relMerchantCode;
    }

    public String getRelShopCode() {
        return relShopCode;
    }

    public void setRelShopCode(String relShopCode) {
        this.relShopCode = relShopCode;
    }

    public String getRelChannelShopCode() {
        return relChannelShopCode;
    }

    public void setRelChannelShopCode(String relChannelShopCode) {
        this.relChannelShopCode = relChannelShopCode;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getFulfillTagFromTrade() {
        return fulfillTagFromTrade;
    }

    public void setFulfillTagFromTrade(String fulfillTagFromTrade) {
        this.fulfillTagFromTrade = fulfillTagFromTrade;
    }

    public Date getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Date planTime) {
        this.planTime = planTime;
    }

    public String getSceneCode() {
        return sceneCode;
    }

    public void setSceneCode(String sceneCode) {
        this.sceneCode = sceneCode;
    }

    public BigDecimal getExpectSaleQuantity() {
        return expectSaleQuantity;
    }

    public void setExpectSaleQuantity(BigDecimal expectSaleQuantity) {
        this.expectSaleQuantity = expectSaleQuantity;
    }

    public void setTradeSubBusinessType(Integer tradeSubBusinessType) {
        this.tradeSubBusinessType = tradeSubBusinessType;
    }

    public String getIsHold() {
        return isHold;
    }

    public void setIsHold(String isHold) {
        this.isHold = isHold;
    }
}
