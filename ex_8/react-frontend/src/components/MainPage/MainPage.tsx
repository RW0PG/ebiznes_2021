import React, {FC} from 'react';
import {inject, observer} from 'mobx-react';
import {useHistory} from 'react-router';
import {AppBar, IconButton, Toolbar, Typography} from '@material-ui/core';
import Cookies from 'js-cookie';
import {RootStore} from '../../stores/RootStore';
import {SignOutUser} from "../../api/userService";


export const MainPage: FC<{store?: RootStore}> = inject("store")(observer(({store, children}) => {
	const userStore = store?.userStore
	const history = useHistory()

	const signOut = async () => {
		if (userStore?.user && userStore?.password) {
			try {
				const res = await SignOutUser(userStore.user.email, userStore.password)
				console.log(res)
			} catch (e) {
				console.log(e)
			}
		}
		userStore?.clear()
		Cookies.remove('csrfToken')
		Cookies.remove('PLAY_SESSION')
		Cookies.remove('OAuth2State')
		Cookies.remove('authenticator')
		Cookies.remove('userId')
		window.location.reload()
	}

	return (
		<>
			<AppBar position="static">
				<Toolbar>
					<IconButton edge="start" onClick={() => history.push('/')}>
						<img height='55px' src='images/kouros.png' alt='store'/>
					</IconButton>
					<Typography style={{flexGrow: 1, fontWeight: 1000, fontSize: 28, textAlign: 'center'}} color='inherit'>
						Fragrance Store
					</Typography>
						<div>
							<IconButton color='inherit' onClick={() => history.push('/cart')}>
								<img height='50px' src='images/basket.png' alt='store'/>
							</IconButton>
							<IconButton onClick={() => {
								if (!userStore?.user) {
									history.push('/history')
									history.push('/login')
								} else {
									history.push('/history')
								}
							}}>
								<img height='50px' src='images/user-icon.png' alt='store'/>
							</IconButton>
							<IconButton onClick={() => signOut()}>
								<img height="40px" src="images/logout.png" alt="store"/>
							</IconButton>
						</div>
				</Toolbar>
			</AppBar>
			{children}
		</>
	);
}));
