/**
 * 
 */
package com.fijo.cores.web.filter;

import java.util.Collection;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

import com.fijo.cores.admin.authority.service.impl.JDBCRealm;
import com.fijo.cores.admin.authority.service.impl.SNSRealm;
import com.fijo.cores.admin.authority.service.impl.SSORealm;

/**
 * @author zhangbo
 *
 */
public class MutilModularRealmAuthenticator extends ModularRealmAuthenticator {

	public MutilModularRealmAuthenticator() {
		super();
	}

	@Override
	protected AuthenticationInfo doMultiRealmAuthentication(
			Collection<Realm> realms, AuthenticationToken token) {
		SSORealm ssoRealm = null;
		SNSRealm snsRealm = null;
		JDBCRealm jdbcRealm = null;

		for (Realm realm : realms) {
			if (realm instanceof SSORealm) {
				ssoRealm = (SSORealm) realm;
			} else if (realm instanceof SNSRealm) {
				snsRealm = (SNSRealm) realm;
			} else {
				jdbcRealm = (JDBCRealm) realm;
			}
		}

        //核心思想，判断token类型，选择realm
		if(token instanceof SNSAuthenticationToken)
			return doSingleRealmAuthentication(snsRealm, token);
		else if(token instanceof SSOAuthenticationToken)
			return doSingleRealmAuthentication(ssoRealm, token);
		else
			return doSingleRealmAuthentication(jdbcRealm, token);
	}
}
