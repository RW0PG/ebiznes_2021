import React, {FC} from 'react';
import {useHistory} from 'react-router';
import {Button} from "react-bootstrap";
import {Form, Formik, FormikHelpers} from 'formik';
import {SignInStyled} from "../../../styles/SignInStyled";
import {RootStore} from '../../../stores/RootStore';
import {inject, observer} from 'mobx-react';
import {GoogleLoginButton} from 'react-social-login-buttons';
import {HOST} from "../../../api/userService";
import {EmailPassword} from "../SignUp/SignUp";


export interface LoginProps {}

export const SignIn: FC<{ store?: RootStore }> = inject('store')(observer(({store}) => {
    const history = useHistory();
    const userStore = store?.userStore;

    const onSubmit = async (data: {
        email: string,
        password: string,
    }, actions: FormikHelpers<any>) => {
        actions.resetForm();
        try {
            const res = await userStore?.signIn(data.email, data.password);
            console.log(res)
            history.goBack()
        } catch (e) {
            console.log(e)
        }
    };

    const navigateTo = () => {
        window.location.assign(HOST + '/authenticate/google')
    }

    return (
        <SignInStyled>
            <div className="entries">
                <Formik initialValues={{
                    email: '',
                    password: ''
                }} onSubmit={(values, actions) => onSubmit(values, actions)}>
                    {({errors, touched, values, isSubmitting}) => (
                        <Form className="registerForm">
                            <EmailPassword errors={errors} touched={touched}/>
                            <div className="row">
                                <Button type='submit' disabled={isSubmitting}
                                        variant="dark">Sign in</Button>
                            </div>
                            <div className="col">
                                <div className="row">
                                    <Button onClick={() => history.push('/register')} disabled={isSubmitting}
                                            variant="secondary">Sign up</Button>
                                </div>
                                <div className="mt-3 mb-3">
                                    <GoogleLoginButton
                                        style={{height: '40px', fontSize: '17px', width: '100%', margin: 0}}
                                        onClick={() => navigateTo()}/>
                                </div>
                            </div>
                        </Form>
                    )}
                </Formik>
            </div>
        </SignInStyled>
    )
}))

