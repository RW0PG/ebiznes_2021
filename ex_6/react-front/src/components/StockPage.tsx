import React, {FC, useEffect} from 'react';
import {Redirect, Route, Switch, useHistory, useRouteMatch} from 'react-router';
import {StorePage} from '../context/StorePage/StorePage';
import {CartPage} from '../context/CartPage/CartPage';
import {OrderPage} from '../context/OrderPage/OrderPage';
import {LoginPage} from '../context/LoginPage/LoginPage';
import {RegisterPage} from '../context/RegisterPage/RegisterPage';
import {OrderHistoryPage} from '../context/OrderHistoryPage/OrderHistoryPage';
import {RootStore} from '../stores/RootStore';
import {inject, observer} from 'mobx-react';

export const StockPage = () => {

	return (
		<>
			<Routes/>
		</>
	);
};

export const Routes: FC<{ store?: RootStore }> = inject('store')(observer(({store}) => {
	const {path} = useRouteMatch();
	const userStore = store?.userStore
	const history = useHistory()

	useEffect(() => {
		if (!userStore?.user) {
			history.push('/')
		}
	}, [])

	return (
		<Switch>
			<Route path={path} exact component={StorePage}/>
			<Route path={path + 'cart'} component={CartPage}/>
			<Route path={path + 'order'} component={OrderPage}/>
			<Route path={path + 'login'} component={LoginPage}/>
			<Route path={path + 'history'} component={OrderHistoryPage}/>
			<Route path={path + 'register'} component={RegisterPage}/>

			<Route exact path='*'>
				<Redirect to={{pathname: ''}}/>
			</Route>
		</Switch>
	);
}));
