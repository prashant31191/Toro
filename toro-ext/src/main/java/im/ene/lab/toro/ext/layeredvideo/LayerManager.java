/*
 * Copyright 2016 eneim@Eneim Labs, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.ene.lab.toro.ext.layeredvideo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.widget.FrameLayout;
import im.ene.lab.toro.player.ExoVideo;
import im.ene.lab.toro.player.internal.ExoMediaPlayer;
import im.ene.lab.toro.player.internal.RendererBuilderFactory;
import im.ene.lab.toro.player.widget.MediaPlayerController;
import java.util.List;

/**
 * This is the basis for building a layered video player
 * (i.e. a video player with views overlaid on top of it).
 *
 * <p>Given a {@link FrameLayout}, a {@link ExoVideo}, and a set of {@link Layer} objects, the
 * {@link LayerManager} will create an {@link ExoMediaPlayer} for the {@link ExoVideo} object and
 * create each {@link Layer} object's view and overlay it on the {@link FrameLayout} object.
 *
 * <p>Look at {@link SimpleVideoPlayer} to see {@link LayerManager} in action.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)  //
public class LayerManager {

  /**
   * The activity that will display the video.
   */
  private Activity activity;

  /**
   * All the views created by the {@link Layer} objects will be
   * overlaid on this container.
   */
  private final FrameLayout container;

  /**
   * Allows controlling video playback, reading video state, and registering callbacks for state
   * changes.
   */
  private MediaPlayerController control;

  /**
   * Wrapper around ExoPlayer, which is the underlying video player.
   */
  private ExoMediaPlayer exoPlayer;

  /**
   * Given a container, create the video layers and add them to the container.
   *
   * @param activity The activity which will display the video player.
   * @param container The frame layout which will contain the views.
   * @param video the video that will be played by this LayerManager.
   * @param layers The layers which should be displayed on top of the container.
   */
  public LayerManager(Activity activity, FrameLayout container, ExoVideo video, List<Layer> layers) {
    this.activity = activity;
    this.container = container;

    ExoMediaPlayer.RendererBuilder rendererBuilder =
        RendererBuilderFactory.createRendererBuilder(activity, video);

    this.exoPlayer = new ExoMediaPlayer(rendererBuilder);
    this.exoPlayer.prepare();

    this.control = new MediaPlayerController(exoPlayer);

    // Put the layers into the container.
    container.removeAllViews();
    for (Layer layer : layers) {
      container.addView(layer.createView(this));
      layer.onLayerDisplayed(this);
    }
  }

  /**
   * Returns the activity which displays the video player created by this {@link LayerManager}.
   */
  public Activity getActivity() {
    return activity;
  }

  /**
   * Returns the {@link FrameLayout} which contains the views of the {@link Layer}s that this
   * {@link LayerManager} manages.
   */
  public FrameLayout getContainer() {
    return container;
  }

  /**
   * Returns the {@link MediaPlayerController} which can be used to control the video playback
   * (ex. pause, play, seek).
   */
  public MediaPlayerController getControl() {
    return control;
  }

  /**
   * Returns the wrapper which ties the video player to
   * {@link com.google.android.exoplayer.ExoPlayer}.
   */
  public ExoMediaPlayer getExoPlayer() {
    return exoPlayer;
  }

  /**
   * When the video player is no longer needed, call this method.
   */
  public void release() {
    container.removeAllViews();
    if (exoPlayer != null) {
      exoPlayer.release();
      exoPlayer = null;
    }
  }
}
