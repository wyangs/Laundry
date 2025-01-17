package cn.fuego.laundry.ui.user;

import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.fuego.common.log.FuegoLog;
import cn.fuego.common.util.format.DateUtil;
import cn.fuego.common.util.validate.ValidatorUtil;
import cn.fuego.laundry.R;
import cn.fuego.laundry.cache.AppCache;
import cn.fuego.laundry.ui.base.BaseActivtiy;
import cn.fuego.laundry.webservice.up.model.ModifyCustomerReq;
import cn.fuego.laundry.webservice.up.model.base.CustomerJson;
import cn.fuego.laundry.webservice.up.rest.WebServiceContext;
import cn.fuego.misp.constant.MispCommonDataSource;
import cn.fuego.misp.service.http.MispHttpMessage;
import cn.fuego.misp.tool.MispLocationService;
import cn.fuego.misp.ui.pop.MispDataSelector;
import cn.fuego.misp.ui.pop.MispDatePicker;
import cn.fuego.misp.ui.pop.MispPopWindowListener;

public class UserEditActivity extends BaseActivtiy    
{
	private FuegoLog log = FuegoLog.getLog(getClass());
	private CustomerJson customer;
 	private TextView userName;
	private TextView sex;

	private TextView birthday;
	private TextView phone;
	private TextView email;
	private TextView addr;
	

	@Override
	public void handle(MispHttpMessage message)
	{
		super.showMessage(message);
		if(message.isSuccess())
		{
			AppCache.getInstance().update(customer);
			this.finish();
			
		}
		
		
	}

	@Override
	public void initRes()
	{
		this.activityRes.setName("用户信息修改");
		this.activityRes.setAvtivityView(R.layout.user_info_edit);
		this.activityRes.getButtonIDList().add(R.id.misp_title_save);
		this.activityRes.getButtonIDList().add(R.id.user_set_location_addr_btn);

		
		
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		CustomerJson customer = AppCache.getInstance().getCustomer();
		if(null != customer)
		{
			userName = (TextView) findViewById(R.id.user_user_name_text);
			userName.setText(customer.getCustomer_name());
 
			
		    sex = (TextView) findViewById(R.id.user_user_sex_text);
			sex.setText(customer.getCustomer_sex());
			sex.setClickable(true);
			sex.setFocusable(false);
			sex.setOnClickListener(this);

			birthday = (TextView) findViewById(R.id.user_user_birthday_text);
			birthday.setText(customer.getBirthday());
			birthday.setClickable(true);
			birthday.setFocusable(false);
			birthday.setOnClickListener(this);

			phone = (TextView) findViewById(R.id.user_user_phone_text);
			phone.setText(customer.getPhone());

			email = (TextView) findViewById(R.id.user_user_email_text);
			email.setText(customer.getCustomer_email());

			addr = (TextView) findViewById(R.id.user_user_addr_text);
			addr.setText(customer.getAddr());
		}
		
		this.getButtonByID(R.id.user_set_location_addr_btn).requestFocus();
		this.getButtonByID(R.id.user_set_location_addr_btn).requestFocusFromTouch();
		
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.misp_title_save:
			{
				ModifyCustomerReq req = new ModifyCustomerReq();
				customer = AppCache.getInstance().getCustomer().clone();
				
				customer.setCustomer_sex(sex.getText().toString().trim());
				String userNameStr  = userName.getText().toString().trim();
				
				if(ValidatorUtil.isEmpty(userNameStr))
				{
					showMessage("姓名为必填项");
					return;
				}
				customer.setCustomer_name(userNameStr);
 
				
				
				String phoneStr = phone.getText().toString().trim();
				if(ValidatorUtil.isEmpty(phoneStr))
				{
					showMessage("电话为必填项");
					return;
				}
				if(!ValidatorUtil.isPhone(phoneStr))
				{
					showMessage("电话格式不正确");
					return;
				}
				customer.setPhone(phoneStr);
				
				String emailStr = email.getText().toString().trim();
				if(!ValidatorUtil.isEmpty(emailStr))
				{
					if(!ValidatorUtil.isEmail(emailStr))
					{
						showMessage("邮箱格式不正确");
						return;
					}
				}

				customer.setCustomer_email(emailStr);

				customer.setBirthday(birthday.getText().toString().trim());
				
				String addrStr = addr.getText().toString().trim();

				if(!ValidatorUtil.isLength(addrStr, 0, 50))
				{
					showMessage("地址长度不能大于50");
					return;
				}
				customer.setAddr(addrStr);
				req.setObj(customer);
				
			    WebServiceContext.getInstance().getCustomerManageRest(this).modify(req);
			}
			
			break;
			case R.id.user_set_location_addr_btn:
			{
 				MispLocationService.getInstance().setLocationAddr(getApplicationContext(), addr);
			}
			break;
			case R.id.user_user_birthday_text:
			{
				showDatePickerDialog(this.birthday);
			}
			break;
			case R.id.user_user_sex_text:
			{
				
				MispDataSelector.getInstance().selectItem("性别", this, MispCommonDataSource.getSexDataSource(), this.sex);
			}
				
			break;
		default:
			break;
		}

	}
	
	public void showDatePickerDialog(final TextView view)
	{  
		String str = view.getText().toString();
 
		
		Date date = DateUtil.shortStrToDate(str);
		MispDatePicker datePicker = new MispDatePicker(new MispPopWindowListener()
		{
			
			@Override
			public void onConfirmClick(String value)
			{
				view.setText(value);
				
			}
		},date);  
	    datePicker.show(getFragmentManager(), "datePicker");
  	}
 


}
