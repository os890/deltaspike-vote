/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.deltaspike.proxy.impl.enableinterceptors;

import javax.inject.Inject;
import org.apache.deltaspike.test.proxy.impl.util.ArchiveUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class EnableInterceptorsTest
{    
    @Deployment
    public static WebArchive war()
    {
        Asset beansXml = new StringAsset(
            "<beans><interceptors><class>" +
                    MyBeanInterceptor.class.getName() +
            "</class></interceptors></beans>"
        );

        String simpleName = EnableInterceptorsTest.class.getSimpleName();
        String archiveName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);

        JavaArchive testJar = ShrinkWrap.create(JavaArchive.class, archiveName + ".jar")
                .addPackage(EnableInterceptorsTest.class.getPackage())
                .addAsManifestResource(beansXml, "beans.xml");

        return ShrinkWrap.create(WebArchive.class, archiveName + ".war")
                .addAsLibraries(ArchiveUtils.getDeltaSpikeCoreAndProxyArchive())
                .addAsLibraries(testJar)
                .addAsWebInfResource(beansXml, "beans.xml");
    }

    @Inject
    private MyBean myBean;
    
    @Test
    public void testInterception() throws Exception
    {
        Assert.assertFalse(myBean.isIntercepted());
        Assert.assertFalse(myBean.isMethodCalled());
        
        myBean.somethingIntercepted();
        
        Assert.assertTrue(myBean.isIntercepted());
        Assert.assertTrue(myBean.isMethodCalled());
    }
    
    @Test
    public void testNonInterception() throws Exception
    {
        Assert.assertFalse(myBean.isIntercepted());
        Assert.assertFalse(myBean.isMethodCalled());
        
        myBean.somethingNotIntercepted();
        
        Assert.assertFalse(myBean.isIntercepted());
        Assert.assertTrue(myBean.isMethodCalled());
    }
}
