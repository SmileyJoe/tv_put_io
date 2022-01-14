package io.smileyjoe.putio.tv.action.video;

public interface Convert {

    //        default void onConvert(){
//            Putio.convertFile(getBaseContext(), getVideo().getPutId(), result -> Putio.getConversionStatus(getBaseContext(), getVideo().getPutId(), new VideoDetailsFragment.OnConvertResponse()););
//        }

    //    private static class OnConvertResponse extends Response {
//        @Override
//        public void onSuccess(JsonObject result) {
//            // todo: this isn't working correctly //
//            VideoDetailsFragment detailsFragment = (VideoDetailsFragment) getFragmentManager().findFragmentById(R.id.details_fragment);
//            detailsFragment.conversionStarted();
//        }
//    }

//    private static class OnConvertResponse extends Response {
//        @Override
//        public void onSuccess(JsonObject result) {
//            int percentDone = -1;
//
//            try {
//                percentDone = result.get("mp4").getAsJsonObject().get("percent_done").getAsInt();
//            } catch (UnsupportedOperationException | NullPointerException e) {
//
//            }
//
//            if (percentDone >= 0) {
//                Action action = getAction(DetailsAction.CONVERT.getId());
//
//                if (action != null) {
//                    if (percentDone < 100) {
//                        action.setLabel2(percentDone + "%");
//                        updateActions(action);
//
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                getConversionStatus();
//                            }
//                        }, 1000);
//                    } else {
//                        mActionAdapter.remove(action);
//                        updateActions(action);
//                        mVideo.setConverted(true);
//                    }
//                }
//            }
//        }
//    }

    //    private void getConversionStatus() {
//        if (!mVideo.isConverted()) {
//            Putio.getConversionStatus(getContext(), mVideo.getPutId(), new OnConvertResponse());
//        }
//    }

}
