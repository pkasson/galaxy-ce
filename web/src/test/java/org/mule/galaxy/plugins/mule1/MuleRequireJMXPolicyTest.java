package org.mule.galaxy.plugins.mule1;

import org.mule.galaxy.mule1.policy.RequireJMXPolicy;
import org.mule.galaxy.plugins.AbstractPolicyTest;

public class MuleRequireJMXPolicyTest extends AbstractPolicyTest
{

    public void testRequiresJMXAgentNotDefined() throws Exception
    {
        doPolicyTest(getResourceAsStream("/mule/policy/require-jmx-policy-test1.xml"), RequireJMXPolicy.ID, true);
    }

    public void testRequiresJMXOk() throws Exception
    {
        doPolicyTest(getResourceAsStream("/mule/policy/require-jmx-policy-test2.xml"), RequireJMXPolicy.ID, false);
    }
}