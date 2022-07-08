package com.jakewharton.rxbinding.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.UiThreadTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import com.jakewharton.rxbinding.RecordingObserver;
import com.jakewharton.rxbinding.test.R;
import com.jakewharton.rxbinding.view.MenuItemActionViewEvent.Kind;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Subscription;
import rx.functions.Func1;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class) public final class RxMenuItemTest {
  @Rule public final UiThreadTestRule uiThread = new UiThreadTestRule();

  private final Context context = ApplicationProvider.getApplicationContext();
  private final TestMenuItem menuItem = new TestMenuItem(context);

  @Test @UiThreadTest public void clicks() {
    RecordingObserver<Void> o = new RecordingObserver<>();
    Subscription subscription = RxMenuItem.clicks(menuItem).subscribe(o);
    o.assertNoMoreEvents(); // No initial value.

    menuItem.performClick();
    assertThat(o.takeNext()).isNull();

    menuItem.performClick();
    assertThat(o.takeNext()).isNull();

    subscription.unsubscribe();

    menuItem.performClick();
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void clicksAvoidHandling() {
    Func1<MenuItem, Boolean> handled = new Func1<MenuItem, Boolean>() {
      @Override public Boolean call(MenuItem menuItem) {
        return Boolean.FALSE;
      }
    };

    RecordingObserver<Object> o = new RecordingObserver<>();
    Subscription subscription = RxMenuItem.clicks(menuItem, handled).subscribe(o);
    o.assertNoMoreEvents(); // No initial value.

    menuItem.performClick();
    o.assertNoMoreEvents();

    menuItem.performClick();
    o.assertNoMoreEvents();

    subscription.unsubscribe();

    menuItem.performClick();
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void actionViewEvents() {
    RecordingObserver<MenuItemActionViewEvent> o = new RecordingObserver<>();
    Subscription subscription = RxMenuItem.actionViewEvents(menuItem).subscribe(o);
    o.assertNoMoreEvents(); // No initial value.

    menuItem.expandActionView();
    assertThat(o.takeNext()).isEqualTo(MenuItemActionViewEvent.create(menuItem, Kind.EXPAND));

    menuItem.collapseActionView();
    assertThat(o.takeNext()).isEqualTo(MenuItemActionViewEvent.create(menuItem, Kind.COLLAPSE));

    subscription.unsubscribe();

    menuItem.performClick();
    o.assertNoMoreEvents();
  }

  @Test @UiThreadTest public void actionViewEventsAvoidHandling() {
    Func1<MenuItemActionViewEvent, Boolean> handled =
        new Func1<MenuItemActionViewEvent, Boolean>() {
          @Override public Boolean call(MenuItemActionViewEvent menuItem) {
            return Boolean.FALSE;
          }
        };

    RecordingObserver<MenuItemActionViewEvent> o = new RecordingObserver<>();
    Subscription subscription = RxMenuItem.actionViewEvents(menuItem, handled).subscribe(o);
    o.assertNoMoreEvents(); // No initial value.

    menuItem.expandActionView();
    assertThat(menuItem.isActionViewExpanded()).isEqualTo(false); // Should be prevented by handler
    o.assertNoMoreEvents();

    subscription.unsubscribe();

    menuItem.performClick();
    o.assertNoMoreEvents();
  }

  @Test public void checked() {
    menuItem.setCheckable(true);
    RxMenuItem.checked(menuItem).call(true);
    assertThat(menuItem.isChecked()).isEqualTo(true);
    RxMenuItem.checked(menuItem).call(false);
    assertThat(menuItem.isChecked()).isEqualTo(false);
  }

  @Test public void enabled() {
    RxMenuItem.enabled(menuItem).call(true);
    assertThat(menuItem.isEnabled()).isEqualTo(true);
    RxMenuItem.enabled(menuItem).call(false);
    assertThat(menuItem.isEnabled()).isEqualTo(false);
  }

  @Test public void icon() {
    Drawable drawable = context.getResources().getDrawable(R.drawable.icon);
    RxMenuItem.icon(menuItem).call(drawable);
    assertThat(menuItem.getIcon()).isEqualTo(drawable);
  }

  @Test public void iconRes() {
    ColorDrawable drawable = (ColorDrawable) context.getResources().getDrawable(R.drawable.icon);
    RxMenuItem.iconRes(menuItem).call(R.drawable.icon);
    assertThat(((ColorDrawable) menuItem.getIcon()).getColor()).isEqualTo(drawable.getColor());
  }

  @Test public void title() {
    RxMenuItem.title(menuItem).call("Hey");
    assertThat(menuItem.getTitle()).isEqualTo("Hey");
  }

  @Test public void titleRes() {
    RxMenuItem.titleRes(menuItem).call(R.string.hey);
    assertThat(menuItem.getTitle()).isEqualTo(context.getText(R.string.hey));
  }

  @Test public void visible() {
    RxMenuItem.visible(menuItem).call(true);
    assertThat(menuItem.isVisible()).isEqualTo(true);
    RxMenuItem.visible(menuItem).call(false);
    assertThat(menuItem.isVisible()).isEqualTo(false);
  }

  // There is no accessible default implementation of MenuItem, so we have to create one
  private static final class TestMenuItem implements MenuItem {

    private final Context context;

    private int itemId;
    private int groupId;
    private int order;
    private CharSequence title;
    private CharSequence titleCondensed;
    private Drawable icon;
    private Intent intent;
    private char numericChar;
    private char alphaChar;
    private boolean checkable;
    private boolean checked;
    private boolean visible;
    private boolean enabled;
    private OnMenuItemClickListener menuItemClickListener;
    private int actionEnum;
    private View actionView;
    private ActionProvider actionProvider;
    private boolean isActionViewExpanded;
    private OnActionExpandListener actionExpandListener;

    public TestMenuItem(Context context) {
      this.context = context;
    }

    public void performClick() {
      if (menuItemClickListener != null) {
        menuItemClickListener.onMenuItemClick(this);
      }
    }

    @Override public int getItemId() {
      return itemId;
    }

    @Override public int getGroupId() {
      return groupId;
    }

    @Override public int getOrder() {
      return order;
    }

    @Override public MenuItem setTitle(CharSequence title) {
      this.title = title;
      return this;
    }

    @Override public MenuItem setTitle(int title) {
      this.title = context.getText(title);
      return this;
    }

    @Override public CharSequence getTitle() {
      return title;
    }

    @Override public MenuItem setTitleCondensed(CharSequence title) {
      this.titleCondensed = title;
      return this;
    }

    @Override public CharSequence getTitleCondensed() {
      return titleCondensed;
    }

    @Override public MenuItem setIcon(Drawable icon) {
      this.icon = icon;
      return this;
    }

    @Override public MenuItem setIcon(int iconRes) {
      this.icon = context.getResources().getDrawable(iconRes);
      return this;
    }

    @Override public Drawable getIcon() {
      return icon;
    }

    @Override public MenuItem setIntent(Intent intent) {
      this.intent = intent;
      return this;
    }

    @Override public Intent getIntent() {
      return intent;
    }

    @Override public MenuItem setShortcut(char numericChar, char alphaChar) {
      this.numericChar = numericChar;
      this.alphaChar = alphaChar;
      return this;
    }

    @Override public MenuItem setNumericShortcut(char numericChar) {
      this.numericChar = numericChar;
      return this;
    }

    @Override public char getNumericShortcut() {
      return numericChar;
    }

    @Override public MenuItem setAlphabeticShortcut(char alphaChar) {
      this.alphaChar = alphaChar;
      return this;
    }

    @Override public char getAlphabeticShortcut() {
      return alphaChar;
    }

    @Override public MenuItem setCheckable(boolean checkable) {
      this.checkable = checkable;
      return this;
    }

    @Override public boolean isCheckable() {
      return checkable;
    }

    @Override public MenuItem setChecked(boolean checked) {
      if (checkable) {
        this.checked = checked;
      }
      return this;
    }

    @Override public boolean isChecked() {
      return checked;
    }

    @Override public MenuItem setVisible(boolean visible) {
      this.visible = visible;
      return this;
    }

    @Override public boolean isVisible() {
      return visible;
    }

    @Override public MenuItem setEnabled(boolean enabled) {
      this.enabled = enabled;
      return this;
    }

    @Override public boolean isEnabled() {
      return enabled;
    }

    @Override public boolean hasSubMenu() {
      return false;
    }

    @Override public SubMenu getSubMenu() {
      return null;
    }

    @Override
    public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
      this.menuItemClickListener = menuItemClickListener;
      return this;
    }

    @Override public ContextMenu.ContextMenuInfo getMenuInfo() {
      return null;
    }

    @Override public void setShowAsAction(int actionEnum) {
      this.actionEnum = actionEnum;
    }

    @Override public MenuItem setShowAsActionFlags(int actionEnum) {
      this.actionEnum = actionEnum;
      return this;
    }

    @Override public MenuItem setActionView(View view) {
      this.actionView = view;
      return this;
    }

    @Override public MenuItem setActionView(int resId) {
      this.actionView = LayoutInflater.from(context).inflate(resId, null);
      return this;
    }

    @Override public View getActionView() {
      return actionView;
    }

    @Override public MenuItem setActionProvider(ActionProvider actionProvider) {
      this.actionProvider = actionProvider;
      return this;
    }

    @Override public ActionProvider getActionProvider() {
      return actionProvider;
    }

    @Override public boolean expandActionView() {
      if (isActionViewExpanded) {
        return false;
      }

      if (actionExpandListener != null && !actionExpandListener.onMenuItemActionExpand(this)) {
        return false;
      }

      isActionViewExpanded = true;
      return true;
    }

    @Override public boolean collapseActionView() {
      if (!isActionViewExpanded) {
        return false;
      }

      if (actionExpandListener != null && !actionExpandListener.onMenuItemActionCollapse(this)) {
        return false;
      }

      isActionViewExpanded = false;
      return true;
    }

    @Override public boolean isActionViewExpanded() {
      return isActionViewExpanded;
    }

    @Override public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
      this.actionExpandListener = listener;
      return this;
    }
  }
}
