import React, {FC} from 'react';
import {DialogContentText, Input} from '@material-ui/core';
import {Button} from "react-bootstrap";
import {useHistory} from 'react-router';
import {Field, Form, Formik, FormikHelpers} from 'formik';
import {SignUpStyled} from "../../../styles/SignUpStyled";
import {RootStore} from '../../../stores/RootStore';
import {inject, observer} from 'mobx-react';



export const SignUp: FC<{ store?: RootStore }> = inject('store')(observer(({store}) => {
    const history = useHistory();
    const userStore = store?.userStore;

    const onSubmit = async (data: {
        email: string,
        password: string,
        reenterPassword: string,
    }, actions: FormikHelpers<any>) => {

        if (data.password !== data.reenterPassword) {
            alert('passwords are not the same')
            return
        }

        actions.resetForm();
        try {
            const res = await userStore?.signUp(data.email, data.password);
            console.log(res)
            history.push('/order');
        } catch (e) {
            console.log(e)
        }
    };

    return (
        <SignUpStyled>
            <div className="entries">
                <Formik initialValues={{
                    email: '',
                    password: '',
                    reenterPassword: ''
                }} onSubmit={(values, actions) => onSubmit(values, actions)}>
                    {({errors, touched, values, isSubmitting}) => (
                        <Form className="registerForm">
                            <EmailPassword errors={errors} touched={touched}/>
                            <DialogContentText style={{color: '#FCFDFE'}}>Enter your password again:</DialogContentText>
                            <Field as={Input} style={{backgroundColor: '#52585D', color: '#FCFDFE', width: '100%'}}
                                   name="reenterPassword"
                                   type="password"
                                   required
                                   error={errors.reenterPassword && touched.reenterPassword ? errors.reenterPassword : null}/>
                            <div className="row">
                                <Button type='submit' disabled={isSubmitting}
                                        variant="secondary">Sign up</Button>
                            </div>
                        </Form>
                    )}
                </Formik>
            </div>
        </SignUpStyled>
    );
}));

export const EmailPassword: FC<{errors: any, touched: any}> = ({errors, touched}) => {
    return (
        <>
            <DialogContentText style={{color: '#FCFDFE'}}>Email:</DialogContentText>
            <Field as={Input} style={{backgroundColor: '#52585D', color: '#FCFDFE', width: '100%'}}
                   name="email"
                   required
                   error={errors.email && touched.email ? errors.email : null}/>

            <DialogContentText style={{color: '#FCFDFE'}}>Password:</DialogContentText>
            <Field as={Input} style={{backgroundColor: '#52585D', color: '#FCFDFE', width: '100%'}}
                   name="password"
                   type="password"
                   required
                   error={errors.password && touched.password ? errors.password : null}/>
        </>
    )
}


