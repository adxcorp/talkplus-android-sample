package io.talkplus.sample;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.talkplus.TalkPlus;
import io.talkplus.entity.channel.TPChannel;
import io.talkplus.entity.channel.TPMessage;
import io.talkplus.entity.user.TPUser;
import io.talkplus.sample.base.BaseActivity;
import io.talkplus.util.CommonUtil;
import io.talkplus.util.Logger;

public class ChannelActivity extends BaseActivity {
    private EditText mEditTextMessage;
    private Button mBtnSend;

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter = new MyAdapter();
    private LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

    private List<TPMessage> mMessageList = new ArrayList<>();
    private TPChannel mChannel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mEditTextMessage = findViewById(R.id.editTextMessage);
        mBtnSend = findViewById(R.id.btnSend);
        mBtnSend.setOnClickListener(mClickListener);

        mRecyclerView = findViewById(R.id.recyclerView);
        mLayoutManager.setReverseLayout(true);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!isShowPreviousMessage()) {
                    loadNextMessageList();
                }

                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mChannel = (TPChannel) getIntent().getSerializableExtra("channel");

        TalkPlus.getMessageList(mChannel, null, new TalkPlus.CallbackListener<List<TPMessage>>() {
            @Override
            public void onSuccess(List<TPMessage> tpMessages) {
                Logger.log("getMessageList onSuccess: " + tpMessages);
                mMessageList.addAll(tpMessages);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int errorCode, Exception e) {
                Logger.log("onFailure", e);
            }
        });

        TalkPlus.addChannelListener(mChannel.getChannelId(), new TalkPlus.ChannelListener() {
            @Override
            public void onMemberAdded(TPChannel tpChannel, List<TPUser> list) {
            }

            @Override
            public void onMemberLeft(TPChannel tpChannel, List<TPUser> list) {

            }

            @Override
            public void onMessageReceived(TPChannel tpChannel, TPMessage tpMessage) {
                if (TextUtils.equals(tpChannel.getChannelId(), mChannel.getChannelId())) {
                    addMessageToList(tpMessage);
                    markRead();
                }
            }

            @Override
            public void onChannelAdded(TPChannel tpChannel) {
            }

            @Override
            public void onChannelChanged(TPChannel tpChannel) {
                // TODO: 채널 변경 처리
            }

            @Override
            public void onChannelRemoved(TPChannel tpChannel) {

            }

            @Override
            public void onPublicMemberAdded(TPChannel tpChannel, List<TPUser> list) {

            }

            @Override
            public void onPublicMemberLeft(TPChannel tpChannel, List<TPUser> list) {

            }

            @Override
            public void onPublicChannelAdded(TPChannel tpChannel) {

            }

            @Override
            public void onPublicChannelChanged(TPChannel tpChannel) {

            }

            @Override
            public void onPublicChannelRemoved(TPChannel tpChannel) {
            }
        });
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.channel_menu_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu5) {
            leaveChannel();
        }
//        if (id == R.id.menu1) {
//            Intent intent = new Intent(MainActivity.this, InviteActivity.class);
//            startActivityForResult(intent, 0x1818);
//        } else if (id == R.id.menu2) {
//            Intent intent = new Intent(MainActivity.this, InviteActivity.class);
//            startActivityForResult(intent, 0x2828);
//        } else if (id == R.id.menu3) {
//            Intent intent = new Intent(MainActivity.this, InviteActivity.class);
//            startActivityForResult(intent, 0x3838);
//        } else if (id == R.id.menu4) {
//            joinPublicChannel();
//        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isLoading = false;

    private void loadNextMessageList() {
        if (isLoading) {
            return;
        }

        isLoading = true;

        if (mMessageList.size() > 0) {
            showProgressDialog();
            TalkPlus.getMessageList(mChannel, mMessageList.get(mMessageList.size() - 1), new TalkPlus.CallbackListener<List<TPMessage>>() {
                @Override
                public void onSuccess(List<TPMessage> tpMessages) {
                    dismissProgressDialog();

                    isLoading = false;

                    mMessageList.addAll(tpMessages);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(int errorCode, Exception e) {
                    dismissProgressDialog();

                    isLoading = false;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        markRead();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        TalkPlus.removeChannelListener(mChannel.getChannelId());
    }

    private void markRead() {
        TalkPlus.markAsReadChannel(mChannel, new TalkPlus.CallbackListener<TPChannel>() {
            @Override
            public void onSuccess(TPChannel tpChannel) {
                mChannel = tpChannel;

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int errorCode, Exception e) {
            }
        });
    }

    private void leaveChannel() {
        TalkPlus.leaveChannel(mChannel, true, new TalkPlus.CallbackListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Logger.log("leaveChannel onSuccess");
                finish();
            }

            @Override
            public void onFailure(int errorCode, Exception e) {
                Logger.log("leaveChannel onFailure");
            }
        });
    }

    private boolean isShowPreviousMessage() {
        return mLayoutManager.findLastVisibleItemPosition() < mLayoutManager.getItemCount() - 1;
    }

    private void addMessageToList(TPMessage tpMessage) {
        if (tpMessage == null) {
            return;
        }

        mMessageList.add(0, tpMessage);
        mAdapter.notifyDataSetChanged();

        mRecyclerView.scrollToPosition(0);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String message = mEditTextMessage.getText().toString();

            if (!TextUtils.isEmpty(message)) {
                mEditTextMessage.setText("");

                TalkPlus.sendMessage(mChannel, message, TPMessage.TYPE_TEXT, null, new TalkPlus.CallbackListener<TPMessage>() {
                    @Override
                    public void onSuccess(TPMessage tpMessage) {
                        Logger.log("sendMessage onSuccess");
                        addMessageToList(tpMessage);
                    }

                    @Override
                    public void onFailure(int errorCode, Exception e) {
                        Logger.log("sendMessage onFailure");
                    }
                });
            }
        }
    };

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView textSender;
            public TextView textMessage;
            public TextView textMessageTime;
            public TextView textUnreadCount;

            public View view;

            public MyViewHolder(View v) {
                super(v);

                view = v;
                textSender = v.findViewById(R.id.textSender);
                textMessage = v.findViewById(R.id.textMessage);
                textMessageTime = v.findViewById(R.id.textMessageTime);
                textUnreadCount = v.findViewById(R.id.textUnreadCount);
            }
        }

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ChannelActivity.this).inflate(R.layout.layout_chat_list_item, parent, false);

            MyAdapter.MyViewHolder vh = new MyAdapter.MyViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(MyAdapter.MyViewHolder holder, int position) {
            final TPMessage tpMessage = mMessageList.get(position);
            holder.textSender.setText(tpMessage.getUsername());
            holder.textMessage.setText(tpMessage.getText());
            holder.textMessageTime.setText(CommonUtil.getFormattedTime(tpMessage.getCreatedAt()));
            holder.textUnreadCount.setText(String.format("Unread: %d", mChannel.getMessageUnreadCount(tpMessage)));
        }

        @Override
        public int getItemCount() {
            return mMessageList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}
