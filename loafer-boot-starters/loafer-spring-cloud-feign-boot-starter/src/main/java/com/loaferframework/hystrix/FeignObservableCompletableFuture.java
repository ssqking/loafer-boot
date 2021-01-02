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
import rx.Subscription;

import java.util.concurrent.CompletableFuture;

/**
 * <p>
 * 创建时间: 2021年01月02号
 * 联系方式: hchkang8710@gmail.com
 * </p>
 *
 * @author kanghouchao
 * @since 2.0.0
 * @see {@link feign.hystrix.ObservableCompletableFuture}
 */
public class FeignObservableCompletableFuture<T> extends CompletableFuture<T> {

    private final Subscription sub;

    FeignObservableCompletableFuture(final HystrixCommand<T> command) {
        this.sub = command.toObservable().single().subscribe(FeignObservableCompletableFuture.this::complete,
                FeignObservableCompletableFuture.this::completeExceptionally);
    }


    @Override
    public boolean cancel(final boolean b) {
        sub.unsubscribe();
        return super.cancel(b);
    }
}