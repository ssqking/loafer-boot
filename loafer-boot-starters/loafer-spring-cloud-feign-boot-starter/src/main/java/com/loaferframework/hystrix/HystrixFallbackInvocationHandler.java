/*
 * Copyright 2020 the original kanghouchao.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loaferframework.hystrix;

import com.netflix.hystrix.HystrixCommand;
import feign.InvocationHandlerFactory;
import feign.Target;
import feign.Util;
import feign.hystrix.FallbackFactory;
import feign.hystrix.SetterFactory;
import org.springframework.http.ResponseEntity;
import rx.Completable;
import rx.Observable;
import rx.Single;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static feign.Util.checkNotNull;

/**
 * 参照 {@link feign.hystrix.HystrixInvocationHandler} 实现自动降级操作
 * <p>
 * 创建时间: 2021年01月02号
 * 联系方式: hchkang8710@gmail.com
 * </p>
 *
 * @author kanghouchao
 * @since 2.0.0
 */
public class HystrixFallbackInvocationHandler implements InvocationHandler {

    private final Target<?> target;

    private final Map<Method, InvocationHandlerFactory.MethodHandler> dispatch;

    private FallbackFactory fallbackFactory;

    private Map<Method, Method> fallbackMethodMap;

    private final Map<Method, HystrixCommand.Setter> setterMethodMap;

    HystrixFallbackInvocationHandler(Target<?> target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch, SetterFactory setterFactory,
                                     FallbackFactory fallbackFactory) {
        this.target = checkNotNull(target, "target");
        this.dispatch = checkNotNull(dispatch, "dispatch");
        this.fallbackFactory = fallbackFactory;
        this.fallbackMethodMap = toFallbackMethod(dispatch);
        this.setterMethodMap = toSetters(setterFactory, target, dispatch.keySet());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("equals".equals(method.getName())) {
            try {
                Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
                return equals(otherHandler);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if ("hashCode".equals(method.getName())) {
            return hashCode();
        } else if ("toString".equals(method.getName())) {
            return toString();
        }

        HystrixCommand<Object> hystrixCommand = new HystrixCommand<Object>(setterMethodMap.get(method)) {
            @Override
            protected Object run() throws Exception {
                try {
                    return HystrixFallbackInvocationHandler.this.dispatch.get(method).invoke(args);
                } catch (Exception e) {
                    throw e;
                } catch (Throwable t) {
                    throw (Error) t;
                }
            }

            @Override
            protected Object getFallback() {
                try {
                    Object fallback = fallbackFactory.create(getExecutionException());
                    Object result = fallbackMethodMap.get(method).invoke(fallback, args);
                    if (isReturnsHydoskyR(method)) {
                        return result;
                    } else if (isReturnsHystrixCommand(method)) {
                        return ((HystrixCommand) result).execute();
                    } else if (isReturnsObservable(method)) {
                        // Create a cold Observable
                        return ((Observable) result).toBlocking().first();
                    } else if (isReturnsSingle(method)) {
                        // Create a cold Observable as a Single
                        return ((Single) result).toObservable().toBlocking().first();
                    } else if (isReturnsCompletable(method)) {
                        ((Completable) result).await();
                        return null;
                    } else if (isReturnsCompletableFuture(method)) {
                        return ((Future) result).get();
                    } else {
                        return result;
                    }
                } catch (IllegalAccessException e) {
                    // shouldn't happen as method is public due to being an interface
                    throw new AssertionError(e);
                } catch (InvocationTargetException | ExecutionException e) {
                    // Exceptions on fallback are tossed by Hystrix
                    throw new AssertionError(e.getCause());
                } catch (InterruptedException e) {
                    // Exceptions on fallback are tossed by Hystrix
                    Thread.currentThread().interrupt();
                    throw new AssertionError(e.getCause());
                }
            }
        };
        if (isReturnsHydoskyR(method)) {
            return hystrixCommand.execute();
        } else if (Util.isDefault(method)) {
            return hystrixCommand.execute();
        } else if (isReturnsHystrixCommand(method)) {
            return hystrixCommand;
        } else if (isReturnsObservable(method)) {
            // Create a cold Observable
            return hystrixCommand.toObservable();
        } else if (isReturnsSingle(method)) {
            // Create a cold Observable as a Single
            return hystrixCommand.toObservable().toSingle();
        } else if (isReturnsCompletable(method)) {
            return hystrixCommand.toObservable().toCompletable();
        } else if (isReturnsCompletableFuture(method)) {
            return new FeignObservableCompletableFuture<>(hystrixCommand);
        }
        return hystrixCommand.execute();
    }

    static Map<Method, HystrixCommand.Setter> toSetters(SetterFactory setterFactory,
                                                        Target<?> target,
                                                        Set<Method> methods) {
        Map<Method, HystrixCommand.Setter> result = new LinkedHashMap<Method, HystrixCommand.Setter>();
        for (Method method : methods) {
            method.setAccessible(true);
            result.put(method, setterFactory.create(target, method));
        }
        return result;
    }

    static Map<Method, Method> toFallbackMethod(Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
        Map<Method, Method> result = new LinkedHashMap<>();
        for (Method method : dispatch.keySet()) {
            method.setAccessible(true);
            result.put(method, method);
        }
        return result;
    }

    private boolean isReturnsCompletable(Method method) {
        return Completable.class.isAssignableFrom(method.getReturnType());
    }

    private boolean isReturnsHystrixCommand(Method method) {
        return HystrixCommand.class.isAssignableFrom(method.getReturnType());
    }

    private boolean isReturnsObservable(Method method) {
        return Observable.class.isAssignableFrom(method.getReturnType());
    }

    private boolean isReturnsCompletableFuture(Method method) {
        return CompletableFuture.class.isAssignableFrom(method.getReturnType());
    }

    private boolean isReturnsHydoskyR(Method method) {
        return ResponseEntity.class.isAssignableFrom(method.getReturnType());
    }

    private boolean isReturnsSingle(Method method) {
        return Single.class.isAssignableFrom(method.getReturnType());
    }

}
