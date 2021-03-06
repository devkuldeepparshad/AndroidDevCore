package rana.jatin.core.etc;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

import rana.jatin.core.R;
import rana.jatin.core.activity.BaseActivity;
import rana.jatin.core.activity.Extras;
import rana.jatin.core.model.Model;

/**
 * This class implements more simple way to utilize FragmentManager transactions
 */
public class FragmentHelper {
    private final FragmentManager fragmentManager;
    private String TAG = FragmentHelper.class.getName();
    private Fragment fragment;
    private int container;
    private boolean replace = false, addToStack = true;
    private int animExit=R.anim.fragment_out;
    private int animEnter=R.anim.fragment_in;
    private Bundle extras = null;
    private Serializable serializable;
    private Model model;

    public FragmentHelper(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /**
     * Creating new {@link FragmentHelper} for fragmentManager
     * Usage: FragmentHelper.with(this).replace(fragment, R.setExtrasId.container).skipStack().commit();
     *
     * @return new object of {@link FragmentHelper}
     */
    private static FragmentHelper with(FragmentManager fragmentManager) {
        return new FragmentHelper(fragmentManager);
    }

    /**
     * See {@link #with(FragmentManager)}
     *
     * @return new object of {@link FragmentHelper}
     */
    public static FragmentHelper with(AppCompatActivity context) {
        return with(context.getSupportFragmentManager());
    }

    /**
     * See {@link #with(FragmentManager)}
     *
     * @return new object of {@link FragmentHelper}
     */
    public static FragmentHelper with(Fragment fragment) {
        return with(fragment.getFragmentManager());
    }

    /**
     * See {@link #with(FragmentManager)}
     *
     * @return new object of {@link FragmentHelper}
     */
    public static FragmentHelper with(android.app.Fragment fragment) {
        AppCompatActivity appCompatActivity = (AppCompatActivity) fragment.getActivity();
        return with(appCompatActivity);
    }

    /**
     * Same as {@link #fragment(Fragment, int, boolean)} with replace = true
     *
     * @return self
     */
    public FragmentHelper replace(Fragment fragment, @IdRes int container) {
        fragment(fragment, container, true);

        return this;
    }

    /**
     * Same as {@link #fragment(Fragment, int, boolean)} with replace = false
     *
     * @return self
     */
    public FragmentHelper add(Fragment fragment, @IdRes int container) {
        fragment(fragment, container, false);

        return this;
    }

    /**
     * Set fragment to add in transaction
     *
     * @param fragment  fragment to add
     * @param container setExtrasId of view where to add fragment
     * @param replace   true to replace fragment, false to add fragment and hide current
     * @return self
     */
    public FragmentHelper fragment(Fragment fragment, @IdRes int container, boolean replace) {
        this.fragment = fragment;
        this.container = container;
        this.replace = replace;

        return this;
    }

    /**
     * Set flag to not add this transaction to back stack
     * @param skipStack true to not add to back stack
     * @return self
     */
    public FragmentHelper skipStack(boolean skipStack) {
        this.addToStack = !skipStack;
        return this;
    }

    /**
     * Same as {@link #skipStack(boolean)} with true parameter
     * @return self
     */
    public FragmentHelper skipStack() {
        skipStack(true);
        return this;
    }

    /**
     * @param model class which implements serializable passed as arguments to fragment
     * @return self
     */
    public FragmentHelper setModel(Serializable model) {
        this.serializable = model;
        return this;
    }

    /**
     * @param model class which extends {@link Model}passed as arguments to fragment
     * @return self
     */
    public FragmentHelper setModel(Model model) {
        this.model = model;
        return this;
    }

    /**
     * Additional extras to be put into fragment's arguments
     * @param extras extras to add
     * @return self
     */
    public FragmentHelper extras(Bundle extras) {
        this.extras = extras;
        return this;
    }

    /**
     * set custom animations for fragment transactions
     * @param enter animation resource id
     * @param exit animation resource id
     * @return self
     */
    public FragmentHelper setCustomAnimations(int enter,int exit) {
        this.animEnter = enter;
        this.animExit = exit;
        return this;
    }

    /**
     * Compare two fragment to avoid adding the same fragment again
     * @param left
     * @param right
     * @return true if fragment are equal
     */
    private boolean equal(Fragment left, Fragment right) {
        return (left != null && right != null && left.getClass().getName().equalsIgnoreCase(right.getClass().getName()));
    }

    /*
     * Create and commit new transaction executing all collected options.
     * @return true if fragment was added, else false
     */
    public boolean commit() {

        String backStateName = fragment.getClass().getName();
        Fragment current = fragmentManager.findFragmentById(container);
        if (equal(fragment, current))
            return false;

        Bundle args = fragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }

        if (serializable != null)
            args.putSerializable(Extras.MODEL.name(), serializable);

        if (model != null)
            args.putSerializable(Extras.MODEL.name(), model);

        if (extras != null)
            args.putAll(extras);

        fragment.setArguments(args);

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (addToStack)
            transaction.setCustomAnimations(animEnter, animExit, animEnter, animExit);

        if (replace) {
            transaction.replace(container, fragment);
        } else {
            if (current != null)
                transaction.hide(current);
            transaction.add(container, fragment, String.valueOf(container));
        }

        if (addToStack)
            transaction.addToBackStack(backStateName);

        transaction.commitAllowingStateLoss();
        return true;
    }

    /*
    * remove fragment
    * @param fragment to be removed
    * return self
    */
    public FragmentHelper removeFragment(Fragment fragment) {
        if (fragment == null)
            return this;
        try {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.remove(fragment);
            ft.commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        } catch (Exception e) {
        }

        return this;
    }

    /*
    * remove fragment from container
    * @param containerId from fragment to be removed
    * return self
    */
    public FragmentHelper removeFragFromContainer(int containerId) {
        try {
            Fragment fragment = fragmentManager.findFragmentById(container);
            removeFragment(fragment);
        } catch (Exception e) {

        }
        return this;
    }

    /*
    * reload fragment
    * @param fragment to reload
    * return self
    */
    public FragmentHelper reLoadFragment(Fragment fragment) {
        // Reload current fragment
        Fragment frg = null;
        frg = fragmentManager.findFragmentByTag(fragment.getClass().getName());
        frg.onDetach();

        fragmentManager.beginTransaction()
                .detach(frg)
                .commitNowAllowingStateLoss();

        fragmentManager.beginTransaction()
                .attach(frg)
                .commitAllowingStateLoss();
        return this;
    }

    /*
    * remove all the fragments from back stack
    * return self
    */
    public FragmentHelper clearBackStack() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        return this;
    }
}
