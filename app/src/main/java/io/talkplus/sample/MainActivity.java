package io.talkplus.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.talkplus.TalkPlus;
import io.talkplus.entity.channel.TPChannel;
import io.talkplus.entity.user.TPUser;
import io.talkplus.sample.base.BaseActivity;
import io.talkplus.util.CommonUtil;
import io.talkplus.util.Logger;

public class MainActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    private List<TPChannel> mChannelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getChannelList(null);
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu1) {
            Intent intent = new Intent(MainActivity.this, InviteActivity.class);
            startActivityForResult(intent, 0x1818);
        } else if (id == R.id.menu2) {
            Intent intent = new Intent(MainActivity.this, InviteActivity.class);
            startActivityForResult(intent, 0x2828);
        } else if (id == R.id.menu3) {
            Intent intent = new Intent(MainActivity.this, InviteActivity.class);
            startActivityForResult(intent, 0x3838);
        } else if (id == R.id.menu4) {
            joinPublicChannel();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0x1818 && resultCode == RESULT_OK) {
            ArrayList<String> userIds = data.getStringArrayListExtra("userIds");
            createPrivateChannel(userIds);
        } else if (requestCode == 0x2828 && resultCode == RESULT_OK) {
            ArrayList<String> userIds = data.getStringArrayListExtra("userIds");
            createPublicChannel(userIds);
        } else if (requestCode == 0x3838 && resultCode == RESULT_OK) {
            ArrayList<String> userIds = data.getStringArrayListExtra("userIds");
            createInvitationCodeChannel(userIds);
        }
    }

    private void createPrivateChannel(List<String> userIds) {
        TalkPlus.createChannel(userIds, null, true, TPChannel.TYPE_PRIVATE, null, null, null, null, new TalkPlus.CallbackListener<TPChannel>() {
            @Override
            public void onSuccess(TPChannel tpChannel) {
                Logger.log("createChannel onSuccess " + tpChannel);

                mChannelList.add(0, tpChannel);
            }

            @Override
            public void onFailure(int errorCode, Exception e) {
                Logger.log("createChannel onFailure", e);
            }
        });
    }

    private void createPublicChannel(List<String> userIds) {
        TalkPlus.createChannel(userIds, null, true, TPChannel.TYPE_PUBLIC, null, null, null, null, new TalkPlus.CallbackListener<TPChannel>() {
            @Override
            public void onSuccess(TPChannel tpChannel) {
                Logger.log("createChannel onSuccess " + tpChannel);

                mChannelList.add(0, tpChannel);
            }

            @Override
            public void onFailure(int errorCode, Exception e) {
                Logger.log("createChannel onFailure", e);
            }
        });
    }

    private void createInvitationCodeChannel(List<String> userIds) {
        TalkPlus.createChannel(userIds, null, true, TPChannel.TYPE_INVITATION_ONLY, null, "invitationCode", null, null, new TalkPlus.CallbackListener<TPChannel>() {
            @Override
            public void onSuccess(TPChannel tpChannel) {
                Logger.log("createChannel onSuccess " + tpChannel);

                mChannelList.add(0, tpChannel);
            }

            @Override
            public void onFailure(int errorCode, Exception e) {
                Logger.log("createChannel onFailure", e);
            }
        });
    }

    private void joinPublicChannel() {
        TalkPlus.joinChannel("5f586356b136283b55000019", new TalkPlus.CallbackListener<TPChannel>() {
            @Override
            public void onSuccess(TPChannel tpChannel) {
                Logger.log("joinChannel onSuccess");

                getChannelList(tpChannel);
            }

            @Override
            public void onFailure(int errorCode, Exception e) {
                Logger.log("joinChannel onFailure");
            }
        });
    }

    private void getChannelList(TPChannel lastChannel) {
        TalkPlus.getChannelList(lastChannel, new TalkPlus.CallbackListener<List<TPChannel>>() {
            @Override
            public void onSuccess(List<TPChannel> tpChannels) {
                Logger.log("getChannelList channels: " + tpChannels);

                if (mChannelList.size() == 0) {
                    mChannelList = tpChannels;
                } else {
                    mChannelList.addAll(tpChannels);
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int errorCode, Exception e) {
                Logger.log("getChannelList onFailure", e);
            }
        });
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView textAttendees;
            public TextView textLastMessage;
            public TextView textLastMessageTime;
            public TextView textUnreadCount;

            public View view;

            public MyViewHolder(View v) {
                super(v);

                view = v;
                textAttendees = v.findViewById(R.id.textAttendees);
                textLastMessage = v.findViewById(R.id.textLastMessage);
                textLastMessageTime = v.findViewById(R.id.textLastMessageTime);
                textUnreadCount = v.findViewById(R.id.textUnreadCount);
            }
        }

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_room_list_item, parent, false);

            MyViewHolder vh = new MyViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final TPChannel tpChannel = mChannelList.get(position);

            StringBuilder sb = new StringBuilder();

            for (TPUser attendee : tpChannel.getMembers()) {
                sb.append(attendee.getUsername());
                sb.append(",");
            }

            holder.textAttendees.setText(String.format("참여자: %s", sb.toString()));

            if (tpChannel.getLastMessage() != null) {
                holder.textLastMessage.setText(tpChannel.getLastMessage().getText());
                holder.textLastMessageTime.setText(CommonUtil.getFormattedTime(tpChannel.getLastMessage().getCreatedAt()));
            } else {
                holder.textLastMessage.setText("no message");
                holder.textLastMessageTime.setText("");
            }

            holder.textUnreadCount.setText(String.format("unreadCount: %d", tpChannel.getUnreadCount()));

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ChannelActivity.class);
                    intent.putExtra("channel", tpChannel);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mChannelList.size();
        }
    }
}