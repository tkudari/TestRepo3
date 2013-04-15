package com.dashwire.config.util;

import android.content.Context;
import com.dashwire.config.resources.BookmarkConfigurator;
import junit.framework.Assert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * Author: tbostelmann
 */
public class CommonTestUtils {
    private static final int KEEP_ALIVE = 60000;

    public static void waitForBookmarkToBeCreated(final String url, final Context context)
            throws ExecutionException, TimeoutException, InterruptedException {
        waitForBookmarkToBeCreatedOrRemoved(new Boolean(false), url, context);
    }

    public static void waitForBookmarkToBeRemoved(final String url, final Context context)
            throws ExecutionException, TimeoutException, InterruptedException {
        waitForBookmarkToBeCreatedOrRemoved(new Boolean(true), url, context);
    }

    private static void waitForBookmarkToBeCreatedOrRemoved(final Boolean checkCreated, final String url, final Context context)
            throws ExecutionException, TimeoutException, InterruptedException {
        final BookmarkConfigurator bc = new BookmarkConfigurator();
        bc.setContext(context);
        final Callable<Boolean> callable = new Callable<Boolean>() {
            public Boolean call() {
                try {
                    while(checkCreated.equals(bc.bookmarkExists(url))) { }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
        waitForCallable(callable, KEEP_ALIVE);
    }

    public static void waitForAccountToBeCreated(final String account, final Context context)
            throws InterruptedException, ExecutionException, TimeoutException {
        waitForAccountToBeCreatedOrRemoved(false, account, context);
    }

    public static void waitForAccountToBeRemoved(final String account, final Context context)
            throws InterruptedException, ExecutionException, TimeoutException {
        waitForAccountToBeCreatedOrRemoved(true, account, context);
    }

    private static void waitForAccountToBeCreatedOrRemoved(final Boolean checkCreated, final String account, final Context context)
            throws InterruptedException, ExecutionException, TimeoutException {
        final Callable<Boolean> callable = new Callable<Boolean>() {
            public Boolean call() {
                try {
                    while(checkCreated.equals(CommonUtils.accountExists(account, context))) { }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
        waitForCallable(callable, KEEP_ALIVE);
    }

    private static Class clazz = null;
    private static Method method = null;
    public static String getProp(String key)
            throws ClassNotFoundException, InvocationTargetException,
            IllegalAccessException, NoSuchMethodException {
        if (clazz == null || method == null) {
            clazz = Class.forName("android.os.SystemProperties");
            method = clazz.getDeclaredMethod("get", String.class);
        }
        return (String)method.invoke(null, key);
    }

    private static void waitForCallable(final Callable<Boolean> callable, final int keepAlive)
            throws InterruptedException, ExecutionException, TimeoutException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, keepAlive,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        FutureTask<Boolean> future = new FutureTask<Boolean>(callable);
        executor.execute(future);
        Assert.assertTrue(future.get(keepAlive, TimeUnit.MILLISECONDS));
    }
}
