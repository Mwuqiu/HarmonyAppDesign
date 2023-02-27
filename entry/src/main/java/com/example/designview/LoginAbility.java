package com.example.designview;

import com.bumptech.glide.Glide;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.*;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;
import ohos.utils.zson.ZSONArray;
import ohos.utils.zson.ZSONObject;

import java.util.ArrayList;
import java.util.UUID;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;

public class LoginAbility extends Ability implements HttpRequest.Callback{

    private boolean regOrLog;

    Context context = null;

    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00201, "MY_TAG");

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        context = this;

        //注册逻辑设计
        CommonDialog regesterDialog = new CommonDialog(getContext());
        Component regesterContainer = LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_register_dialog, null, false);
        regesterDialog.setContentCustomComponent(regesterContainer);
        regesterDialog.setSize(MATCH_CONTENT,MATCH_CONTENT);
        Button regesterDialogButton = regesterContainer.findComponentById(ResourceTable.Id_btn_regster);
        regesterDialogButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                regesterDialog.destroy();
                //注册情形
                regOrLog = true;
                //点击确认注册按钮发送网络注册请求
                String username = ((TextField)regesterContainer.findComponentById(ResourceTable.Id_reg_username)).getText();
                String password = ((TextField)regesterContainer.findComponentById(ResourceTable.Id_register_userpasswd)).getText();

                String registerString;

                if(URLHELPER.UseMine){
                    registerString = URLHELPER.MineRegister+username+"&password="+password;
                }
                else{
                    registerString = URLHELPER.TeachersRegister+username+"&password="+password;
                }

                //教室云端注册地址
//              String regsterString = "http://101.133.225.23:8080/register?username="+username+"&password="+password;
                // wifi wuqiu 登录访问地址
//              String regsterString = "http://192.168.43.170:8080/login?username="+username+"&password="+password+"&verifyCode="+verifycode;
                //宿舍服务器注册地址
//                String regsterString = "http://49.140.60.34:8080/register?username="+username+"&password="+password;

                //启用线程 调用函数
                TaskDispatcher globalTaskDispatcher = getGlobalTaskDispatcher(TaskPriority.DEFAULT);
                globalTaskDispatcher.asyncDispatch(() -> {
                    findResult(context,registerString);
                });
            }
        });

        //注册按钮跳转逻辑
        Button regesterButton = (Button) findComponentById(ResourceTable.Id_btn_registration);
        regesterButton.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                //点击注册按钮弹出注册界面
                HiLog.error(LABEL,"hit here");
                regesterDialog.show();

            }
        });

        //验证码以及输入验证码后登录逻辑设计
        CommonDialog dialog = new CommonDialog(getContext());
        Component container = LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_layout_custom_dialog, null, false);
        dialog.setContentCustomComponent(container);
        dialog.setSize(MATCH_CONTENT, MATCH_CONTENT);
        Button dialogBtn = container.findComponentById(ResourceTable.Id_dialogbutton);

        //验证码图片显示
        Image image = container.findComponentById(ResourceTable.Id_verifyImg);
        Uri uri = Uri.parse("http://101.133.225.23:8080/images/captcha");
        Glide.with(getContext()).load(uri).into(image);
        image.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                //点击图片更换验证码
                Uri uri = Uri.parse("http://101.133.225.23:8080/images/captcha?d="+ UUID.randomUUID());
                Glide.with(getContext()).load(uri).into((Image)component);
            }
        });

        //输入验证码后登录
        dialogBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                dialog.destroy();
                //非注册情形
                regOrLog = false;

                //点击获取登陆网络请求
                //启用新的线程，获取用户名、密码、和验证码
                String username = ((TextField)findComponentById(ResourceTable.Id_i_username)).getText();
                String password = ((TextField)findComponentById(ResourceTable.Id_i_userpasswd)).getText();
                String verifycode = ((TextField)(container.findComponentById(ResourceTable.Id_verifyCode))).getText();
                String loginString;

                if(URLHELPER.UseMine){
                    loginString = URLHELPER.MineLogin+username+"&password="+password+"&verifyCode="+verifycode;
                }
                else{
                    loginString = URLHELPER.TeachersLogin+username+"&password="+password+"&verifyCode="+verifycode;
                }


                //教师云端  登陆访问地址
//                loginString = "http://101.133.225.23:8080/login2?username="+username+"&password="+password+"&verifyCode="+verifycode;
                // wifi wuqiu 登录访问地址
//                loginString = "http://192.168.43.170:8080/login?username="+username+"&password="+password+"&verifyCode="+verifycode;
                //宿舍有线网 登陆访问地址
//                String loginString = "http://49.140.60.34:8080/login?username="+username+"&password="+password+"&verifyCode="+verifycode;

                TaskDispatcher globalTaskDispatcher = getGlobalTaskDispatcher(TaskPriority.DEFAULT);
                globalTaskDispatcher.asyncDispatch(() -> {
                    findResult(context,loginString);
                });
            }
        });

        //确认登陆按键，显示验证码的弹窗
        Button comfirmBtn = findComponentById(ResourceTable.Id_btn_login);
        comfirmBtn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                String username = ((TextField)findComponentById(ResourceTable.Id_i_username)).getText();
                String password = ((TextField)findComponentById(ResourceTable.Id_i_userpasswd)).getText();
                if (!username.isEmpty() && !password.isEmpty()){
                    dialog.show();
                }
                else{
                    new ToastDialog(context)
                            .setText("Enter Username And Password First!!")
                            .show();
                }
            }
        });
    }


    public void findResult(Context context,String str){
        HttpRequest hr = new HttpRequest();
        //注册和登录都是POST方法
        hr.request(context,str,"POST",this,0);
    }

    @Override
    public void getResult(String ret, int id) {
        //如果不是在注册，则跳转，否则保持在主页面
        if(!regOrLog){
            Intent intent = new Intent();
            // 用来指定 启动的窗口。需要设置设备ID、程序的名称、窗口名称
            Operation operation = new Intent.OperationBuilder()
                    .withDeviceId("")
                    .withBundleName("com.example.designview")
                    .withAbilityName("com.example.designview.FindAbility")
                    .build();

            intent.setOperation(operation);
            startAbility(intent);
            getUITaskDispatcher().asyncDispatch(() -> {
                new ToastDialog(context)
                        .setText("Login Successfully")
                        .show();
            });
        }
        else{
            getUITaskDispatcher().asyncDispatch(() -> {
                new ToastDialog(context)
                        .setText("Register Successfully")
                        .show();
            });
        }
    }

    @Override
    public void getError(String ret, int id) {
    }

}
