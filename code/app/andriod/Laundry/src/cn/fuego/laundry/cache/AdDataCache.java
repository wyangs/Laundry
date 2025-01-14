package cn.fuego.laundry.cache;

import java.util.ArrayList;
import java.util.List;

import cn.fuego.common.log.FuegoLog;
import cn.fuego.common.util.validate.ValidatorUtil;
import cn.fuego.laundry.webservice.up.model.GetADReq;
import cn.fuego.laundry.webservice.up.model.GetADRsp;
import cn.fuego.laundry.webservice.up.model.base.AdvertisementJson;
import cn.fuego.laundry.webservice.up.rest.WebServiceContext;
import cn.fuego.misp.service.http.MispHttpHandler;
import cn.fuego.misp.service.http.MispHttpMessage;

public class AdDataCache
{
	private FuegoLog log = FuegoLog.getLog(getClass());

	private  List<AdvertisementJson> dataList = new ArrayList<AdvertisementJson>();
	private static AdDataCache instance;
	private AdDataCache()
	{
		 
	}
	
	public synchronized static AdDataCache getInstance()
	{
		if(null == instance)
		{
			instance = new AdDataCache();
		}
		return instance;
		
	}
	public void load()
	{
		GetADReq req = new GetADReq();
		
		WebServiceContext.getInstance().getADManageRest(new MispHttpHandler()
		{

			@Override
			public void handle(MispHttpMessage message)
			{
				 if(message.isSuccess())
				 {
					GetADRsp rsp = (GetADRsp) message.getMessage().obj;
					AdDataCache.getInstance().init(rsp.getObj());
				 }
					
			}
			
		}).getAll(req);
	}
	public boolean isEmpty()
	{
		if(ValidatorUtil.isEmpty(dataList))
		{
			return true;
		}
		return false;
	}
	public void init(List<AdvertisementJson> newData)
	{
		dataList.clear();
		if(!ValidatorUtil.isEmpty(newData))
		{
			dataList.addAll(newData);
		}
	}

	public List<AdvertisementJson> getDataList()
	{
		return dataList;
	}
	
	
	
}
