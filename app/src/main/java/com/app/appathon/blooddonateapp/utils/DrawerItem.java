//package com.app.appathon.blooddonateapp.utils;
//
//import android.view.View;
//import android.widget.TextView;
//
//import com.mikepenz.fastadapter.IItem;
//import com.mikepenz.materialdrawer.model.BaseDrawerItem;
//import com.mikepenz.materialdrawer.model.BaseViewHolder;
//
//import java.util.List;
//
//public class DrawerItem extends BaseDrawerItem<DrawerItem, BaseViewHolder> {
//    public static final String TYPE = "DRAWER_ITEM";
//
//    private String title, badge;
//
//    public DrawerItem withTitle(String title) {
//        this.title = title;
//        return this;
//    }
//
//    public DrawerItem withBadge(String badge) {
//        this.badge = badge;
//        return this;
//    }
//
//    @Override
//    public BaseViewHolder getViewHolder(View v) {
//        return null;
//    }
//
//    @Override
//    public int getType() {
//        return 0;
//    }
//
//    @Override
//    public int getLayoutRes() {
//        return 0;
//    }
//
//    @Override
//    public Object withSubItems(List subItems) {
//        return null;
//    }
//
//    @Override
//    public Object withParent(IItem parent) {
//        return null;
//    }
//
//
//    public static class ItemFactory implements ViewHolderFactory<ViewHolder> {
//        public ViewHolder factory(View v) {
//            return new ViewHolder(v);
//        }
//    }
//
//    private static class ViewHolder extends BaseViewHolder {
//        private TextView title, badge;
//
//        public ViewHolder(View view) {
//            super(view);
//        }
//    }
//}