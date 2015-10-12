package com.weixin.perftest;

import com.intuit.sb.dispatcher.DispatcherLib;
import com.intuit.sb.dispatcher.common.domain.RealmHostingDetails;
import com.intuit.sb.dispatcher.common.domain.RealmOffering;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

/**
 * Created by xwei on 10/10/15.
 */
public class DispatcherLibLoadTest extends AbstractJavaSamplerClient {
    private String resultData;
    private String realmId;
    private DispatcherLib lib;

    @Override
    public void setupTest(JavaSamplerContext javaSamplerContext) {
        // setupTest would get called for each thread, create lib instance here
        // for testing purpose, we dont' want cache
        lib = new DispatcherLib("perf", false);
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("companyId", "");

        return params;
    }

    @Override
    public SampleResult runTest(
        JavaSamplerContext javaSamplerContext) {

        realmId = javaSamplerContext.getParameter("companyId");

        SampleResult sr = new SampleResult();
        sr.setSampleLabel("dispatcher lib call");
        RealmOffering ro = new RealmOffering(Long.parseLong(realmId));

        try {
            sr.sampleStart();
            RealmHostingDetails roD = lib.getRealmHostingDetails(ro, true);
            resultData = String.format(
                "companyId : %s, host : %s, datacenter : %s, shard : %s\n", roD.getRealmId(), roD.getHost(),
                roD.getDataCenter(), roD.getShardId());
            if (StringUtils.isNotBlank(resultData)) {
                sr.setResponseData(resultData, null);
                sr.setDataType(SampleResult.TEXT);
                sr.setSuccessful(true);
            }

        } catch (Throwable th) {
            sr.setSuccessful(false);
            th.printStackTrace();
        } finally {
            sr.sampleEnd();
        }

        return sr;
    }

    public void teardownTest(JavaSamplerContext javaSamplerContext){
        // do nothing
    }

/*    public static void main(String[] args) {
        Arguments params = new Arguments();
        params.addArgument("companyId", "1005652640");
        JavaSamplerContext context = new JavaSamplerContext(params);

        DispatcherLibLoadTest test = new DispatcherLibLoadTest();
        test.setupTest(context);
        test.runTest(context);
        test.teardownTest(context);

    }*/
}
