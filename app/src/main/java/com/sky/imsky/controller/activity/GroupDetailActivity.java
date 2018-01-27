package com.sky.imsky.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.sky.imsky.R;
import com.sky.imsky.controller.adapter.GroupDetailAdapter;
import com.sky.imsky.model.Model;
import com.sky.imsky.model.bean.UserInfo;
import com.sky.imsky.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

//群详情页面
public class GroupDetailActivity extends Activity {

    private Unbinder unbinder;
    @BindView(R.id.gv_group_detail)
    GridView gvDetail;
    @BindView(R.id.btn_group_out)
    Button btnOut;
    private String mHxid;
    private EMGroup mGroup;
    private GroupDetailAdapter.OnGroupDetailListener mGroupDetailListener = new GroupDetailAdapter.OnGroupDetailListener() {
        @Override
        public void onAddMembers() {
            Intent intent = new Intent(GroupDetailActivity.this, PickContactActivity.class);
            intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());
            startActivityForResult(intent, 2);
        }

        //删除群成员
        @Override
        public void onDeleteMembers(final UserInfo userInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //从环信服务其中删除此人
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(), userInfo.getHxid());
                        //更新页面
                        getMembersFromHxServer();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除失败" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //获取返回的准备邀请的群成员信息
            final String[] members = data.getStringArrayExtra("members");
            Log.e("members", "==" + members.toString());
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //去环信服务器发送邀请信息
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(), members);
                        //更新页面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送邀请失败" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private List<UserInfo> userInfos;
    private GroupDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        unbinder = ButterKnife.bind(this);

        getData();
        initData();
        //group每一项的监听
        initListener();
    }

    private void initListener() {
        gvDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //判断当前是否是删除模式
                        if (adapter.ismIsDeleteModel()) {
                            //切换为非删除模式
                            adapter.setmIsDeleteModel(false);
                            adapter.notifyDataSetChanged();

                        }
                        break;
                }
                return false;
            }
        });
    }

    private void initData() {
        //初始化button的显示
        initButtonDisplay();

        //初始化gridView
        initGridView();

        //从环信获取所有的群成员
        getMembersFromHxServer();
    }

    private void getMembersFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从环信服务器获取所有的群成员信息
                    EMGroup fromServer = EMClient.getInstance().groupManager().getGroupFromServer(mGroup.getGroupId(), true);
                    List<String> members = fromServer.getMembers();
                    String owner = fromServer.getOwner();
                    Log.e("members", "==" + members.size());
                    if (members != null && members.size() >= 0) {
                        //转换
                        userInfos = new ArrayList<>();
                        for (String member : members) {
                            UserInfo userInfo = new UserInfo(member);
                            userInfos.add(userInfo);
                        }
                    }
                    userInfos.add(0, new UserInfo(owner));
                    //更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.refresh(userInfos);
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this, "获取群信息失败" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initGridView() {
        //当前用户是群主或者群公开了
        boolean isCanModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner()) || mGroup.isPublic();
        adapter = new GroupDetailAdapter(this, isCanModify, mGroupDetailListener);
        gvDetail.setAdapter(adapter);
    }

    private void initButtonDisplay() {
        //如果当前用户为群主
        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())) {
            btnOut.setText("解散群");
            btnOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //去环信服务器解散群
                                EMClient.getInstance().groupManager().destroyGroup(mHxid);

                                //发送退群的广播
                                exitGroupBroadcast();
                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群成功", Toast.LENGTH_SHORT).show();
                                        //结束当前页面
                                        finish();
                                    }
                                });

                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群失败" + e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        } else {
            btnOut.setText("退群");
            btnOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().leaveGroup(mHxid);

                                //发送退群广播
                                exitGroupBroadcast();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群失败" + e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    //发送退群和解散群广播
    private void exitGroupBroadcast() {
        LocalBroadcastManager mLBM = LocalBroadcastManager.getInstance(this);

        Intent intent = new Intent(Constant.EXIT_GROUP);
        intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());
        mLBM.sendBroadcast(intent);
    }

    //获取传递过来的数据
    private void getData() {
        mHxid = getIntent().getStringExtra(Constant.GROUP_ID);
        if (mHxid == null) {
            return;
        } else {
            //拿到当前群的所有相关信息
            mGroup = EMClient.getInstance().groupManager().getGroup(mHxid);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
