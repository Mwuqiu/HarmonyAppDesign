package com.example.designview;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.ListContainer;
import ohos.agp.components.TextField;
import ohos.app.Context;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.zson.ZSONArray;
import ohos.utils.zson.ZSONObject;
import java.util.ArrayList;
import java.util.List;
import com.example.designview.URLHELPER;

public class FindAbility extends Ability implements HttpRequest.Callback {
    ListContainer listContainer;
    Context context = null;

    private static final HiLogLabel LABEL = new HiLogLabel(HiLog.LOG_APP, 0x00201, "MY_TAG");
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        context = this;
        super.setUIContent(ResourceTable.Layout_ability_find);
        // 获取 布局文件中的ListContainer
        listContainer = findComponentById(ResourceTable.Id_list_container);
        // 构造数据
        List<SampleItem> data = getData();
        // 将构造的数据加入到适配器中
        SampleItemProvider sampleItemProvider = new SampleItemProvider(data, this);
        // 将适配器绑定布局文件中的ListContainer
        listContainer.setItemProvider(sampleItemProvider);
        // 开启新线程，调用查询方法
        TaskDispatcher globalTaskDispatcher = getGlobalTaskDispatcher(TaskPriority.DEFAULT);
        globalTaskDispatcher.syncDispatch(() -> {
            //网络访问
            // findResult(this, listContainer);
            findResult2(this,"");
        });

        Button search = findComponentById(ResourceTable.Id_q_btn);
        TextField q = findComponentById(ResourceTable.Id_q_box);
        search.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                /**
                 * 更新的内容
                 * 使用串行任务
                 */
                TaskDispatcher ptd = createSerialTaskDispatcher("name", TaskPriority.DEFAULT);
                ptd.asyncDispatch(() -> {
                    //网络访问
                    // findResult(this, listContainer);
                    findResult2(context, q.getText());

                });
            }
        });


    }

    private ArrayList<SampleItem> getData() {
        ArrayList<SampleItem> list = new ArrayList<>();
        for (int i = 0; i <= 80; i++) {
            list.add(new SampleItem("标题" + i,"内容....."+i));
        }
        return list;
    }

    public void findResult2(Context contextm,String q){
        HttpRequest hr = new HttpRequest();
        String getInformation;

        if(URLHELPER.UseMine){
            getInformation = URLHELPER.MineInfo;
        }
        else {
            getInformation = URLHELPER.TeachersInfo;
        }
        //老师提供的云端服务
//        String getInfomation = "http://101.133.225.23:8080/mInfo?iTitle=";
        //宿舍服务地址
//        String getInfomation = "http://49.140.60.34:8080/mInfo?iTitle=";

        hr.request(context, getInformation+ q,"GET",this,0);
    }

    @Override
    public void getResult(String ret, int id) {
        HiLog.info(LABEL, ret);

        ZSONObject zo = ZSONObject.stringToZSON(ret);
        int code = zo.getIntValue("code");
        System.out.println("---->"+code);

        ArrayList<SampleItem> al = new ArrayList<>();
        ZSONArray list = zo.getZSONArray("data");

        for(int i=0 ; i<list.size();i++){
            ZSONObject z = (ZSONObject) list.get(i);
            String title = z.getString("ititle");
            String content = z.getString("icontent");
            System.out.println("----:第"+i+"项 标题：:"+title);
            al.add(new SampleItem(title,content));
        }

        getUITaskDispatcher().asyncDispatch(() -> {
            //将列表 al 的值，赋给适配器
            // 将构造的数据加入到适配器中
            SampleItemProvider sampleItemProvider = new SampleItemProvider(al, this);
            // 将适配器绑定布局文件中的ListContainer
            listContainer.setItemProvider(sampleItemProvider);
        });
    }

    @Override
    public void getError(String ret, int id) {
    }
}
