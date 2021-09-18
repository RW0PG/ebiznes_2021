import React, {FC, useEffect} from 'react';
import {Redirect, Route, Switch, useHistory, useRouteMatch} from 'react-router';
import {StorePage} from "../context/StorePage/StorePage";
import {CartPage} from "../context/CartPage/CartPage";
import {OrderPage} from "../context/OrderPage/OrderPage";
import {SignInPage} from "../context/SignInPage/SignInPage";
import {SignUpPage} from "../context/SignUpPage/SignUpPage";
import {OrderHistoryPage} from "../context/OrderHistoryPage/OrderHistoryPage";
import {RootStore} from '../stores/RootStore';
import {inject, observer} from 'mobx-react';
import Cookies from 'js-cookie';

export const BasicView = () => {

	return (
		<>
			<Routes/>
		</>
	)
}

export const Routes: FC<{ store?: RootStore }> = inject('store')(observer(({store}) => {
	const {path} = useRouteMatch();
	const userStore = store?.userStore;
	const history = useHistory();

	useEffect(() => {
		if (!userStore?.user) {
			const userId = Cookies.get('userId');
			if (userId) {
				(async () => {
					fetchingUserInCurrentSession(userId)
				})();
			} else {
				const url = new URL(window.location.href);
				const userIdParam = url.searchParams.get("user-id");
				if (userIdParam) {
					(async () => {
						fetchingUserInCurrentSession(userIdParam)
					})();
				}
			}
			history.push('/');
		}
	}, []);

	const fetchingUserInCurrentSession = (userId: string) => {
		try {
			userStore?.getUser(parseInt(userId));
			Cookies.set('userId', userId)
		} catch (e) {
			console.log(e);
		}
	}

	return (
		<Switch>
			<Route path={path} exact component={StorePage}/>
			<Route path={path + 'cart'} component={CartPage}/>
			<Route path={path + 'order'} component={OrderPage}/>
			<Route path={path + 'login'} component={SignInPage}/>
			<Route path={path + 'history'} component={OrderHistoryPage}/>
			<Route path={path + 'register'} component={SignUpPage}/>

			<Route exact path="*">
				<Redirect to={{pathname: ''}}/>
			</Route>
		</Switch>
	);
}));
